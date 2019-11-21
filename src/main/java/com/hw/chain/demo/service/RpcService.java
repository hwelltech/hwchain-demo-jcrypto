package com.hw.chain.demo.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hw.chain.demo.bean.results.AccountInfoResult;
import com.hw.chain.demo.bean.results.TrustSetResult;
import com.hw.chain.demo.utils.OkHttpUtils;

public class RpcService {
	
	/**
     * 获取账号信息
     * @param account
     * @return
     */
    public AccountInfoResult accountInfo(String account) {
        JSONObject requestParam = new JSONObject();
        requestParam.put("method", "account_info");
        JSONObject param = new JSONObject();
        param.put("account", account);
        param.put("strict", true);
        param.put("ledger_index", "current");
        param.put("queue", true);
        JSONArray params = new JSONArray();
        params.add(param);
        requestParam.put("params", params);
        String responseStr = OkHttpUtils.doPost(requestParam);
        JSONObject responseObj = JSONObject.parseObject(responseStr);
        AccountInfoResult accountInfoResult = new AccountInfoResult();
        accountInfoResult.setAccount(responseObj.getJSONObject("result").getJSONObject("account_data").getString("Account"));
        accountInfoResult.setBalance(responseObj.getJSONObject("result").getJSONObject("account_data").getString("Balance"));
        accountInfoResult.setSequence(responseObj.getJSONObject("result").getJSONObject("account_data").getInteger("Sequence"));
        return accountInfoResult;
    }
    
    /**
     * 获取此账号Sequence
     * @param account
     * @return
     */
    public Integer getSequence(String account) {
    	AccountInfoResult accountInfo = accountInfo(account);
		return accountInfo.getSequence();
    }
    
    /**
     * 离线签名提交
     * @param txBlob
     * @return
     */
    public String submit(String txBlob) {
        JSONObject requestParam = new JSONObject();
        requestParam.put("method", "submit");
        JSONArray params = new JSONArray();
        JSONObject param = new JSONObject();
        param.put("tx_blob", txBlob);
        params.add(param);
        requestParam.put("params", params);
        return OkHttpUtils.doPost(requestParam);
    }
    
    /**
     * 授信
     * @param account
     * @return
     */
    public TrustSetResult trustSet(String account, String secret, String currency, String value, String issuer) {
        JSONObject requestParam = new JSONObject();
        requestParam.put("method", "submit");
        JSONObject param = new JSONObject();
        param.put("secret", secret);
        JSONObject txJson = new JSONObject();
        txJson.put("TransactionType", "TrustSet");
        txJson.put("Account", account);
        JSONObject amount = new JSONObject();
        amount.put("currency", currency);
        amount.put("value", value);
        amount.put("issuer", issuer);
        txJson.put("LimitAmount", amount);
        param.put("tx_json", txJson);
        JSONArray params = new JSONArray();
        params.add(param);
        requestParam.put("params", params);
        String responseStr = OkHttpUtils.doPost(requestParam);
        JSONObject responseObj = JSONObject.parseObject(responseStr);
        TrustSetResult trustSetResult = new TrustSetResult();
        trustSetResult.setAccount(responseObj.getJSONObject("result").getJSONObject("tx_json").getString("Account"));
        trustSetResult.setFee(responseObj.getJSONObject("result").getJSONObject("tx_json").getString("Fee"));
        trustSetResult.setHash(responseObj.getJSONObject("result").getJSONObject("tx_json").getString("hash"));
        trustSetResult.setTransactionType(responseObj.getJSONObject("result").getJSONObject("tx_json").getString("TransactionType"));
        trustSetResult.setCurrency(responseObj.getJSONObject("result").getJSONObject("tx_json").getJSONObject("LimitAmount").getString("currency"));
        trustSetResult.setIssuer(responseObj.getJSONObject("result").getJSONObject("tx_json").getJSONObject("LimitAmount").getString("issuer"));
        trustSetResult.setValue(responseObj.getJSONObject("result").getJSONObject("tx_json").getJSONObject("LimitAmount").getString("value"));
        return trustSetResult;
    }
    
    /**
     * 获取此账号交易历史
     * @param account
     * @param limit
     * @return
     */
    public String accountTx(String account, int limit) {
        JSONObject requestParam = new JSONObject();
        requestParam.put("method", "account_tx");
        JSONObject param = new JSONObject();
        param.put("account", account);
        param.put("binary", false);
        param.put("forward", false);
        param.put("ledger_index_max", "-1");
        param.put("ledger_index_min", "-1");
        limit = limit == 0 ? 10 : limit;
        param.put("limit", limit);
        JSONArray params = new JSONArray();
        params.add(param);
        requestParam.put("params", params);
        String responseStr = OkHttpUtils.doPost(requestParam);
        return responseStr;
    }

    /**
     * 根据hash查看交易详情
     * @param hash
     * @return
     */
    public String tx(String hash) {
        JSONObject requestParam = new JSONObject();
        requestParam.put("method", "tx");
        JSONObject param = new JSONObject();
        param.put("transaction", hash);
        param.put("binary", false);
        JSONArray params = new JSONArray();
        params.add(param);
        requestParam.put("params", params);
        return OkHttpUtils.doPost(requestParam);
    }
	
}
