package com.hw.chain.demo;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hw.chain.sdk.crypto.ChainCrypto;
import com.hw.chain.sdk.crypto.codec.types.SignedTransaction;
import com.hw.chain.sdk.crypto.config.CryptoConfig;
import com.hw.chain.sdk.crypto.keypair.KeyPair;
import com.hw.chain.demo.bean.vo.Amount;
import com.hw.chain.demo.bean.results.AccountInfoResult;
import com.hw.chain.demo.bean.vo.AccountKeyPair;
import com.hw.chain.demo.bean.results.PaymentResult;
import com.hw.chain.demo.bean.results.PaymentTokenResult;
import com.hw.chain.demo.cons.Const;
import com.hw.chain.demo.service.RpcService;

/**
 * RPC&离线签名示例
 */
public class DemoApp {
    private static ChainCrypto chainCrypto = new ChainCrypto(new CryptoConfig());
    private static RpcService rpcService = new RpcService();

    public static void main(String[] args) {
        /*
         * 创建帐户
         */
        AccountKeyPair accounts = generate();
        System.out.println("generate: " + JSONObject.toJSONString(accounts));

        /*
         * 转账
         */
        PaymentResult paymentResult = payment("snC***********************KEd", "hLS5GhuGAV11oxz2DW9sZmR12k92TqrNvk", "h9ebaMq3ypXsui5LxYDeVfYHhWA8qBqwVC", "10", "转账备注");
        System.out.println("payment: " + JSONObject.toJSONString(paymentResult));

        /*
         * 二级资产转账
         */
        PaymentTokenResult paymentTokenResult = paymentToken("snC***********************KEd", "hLS5GhuGAV11oxz2DW9sZmR12k92TqrNvk", "h9ebaMq3ypXsui5LxYDeVfYHhWA8qBqwVC", "TOPOT", "10", "hw9PJJZJKPvxoaKNf7DM3F5DHLQSLawfJs", "二级资产转账备注");
        System.out.println("paymentToken: " + JSONObject.toJSONString(paymentTokenResult));


        /*
         * 帐户信息
         */
        AccountInfoResult infoRes = rpcService.accountInfo("h9ebaMq3ypXsui5LxYDeVfYHhWA8qBqwVC");
        System.out.println("accountInfo: " + JSONObject.toJSONString(infoRes));

        /*
         * 交易记录
         */
        String transactionsList = rpcService.accountTx("h9ebaMq3ypXsui5LxYDeVfYHhWA8qBqwVC", 1);
        System.out.println("accountTx: " + transactionsList);

        /*
         * 交易详细
         */
        String transactionsInfo = rpcService.tx("B02196784820F642A039AE426D0E2AB80B383E16C6D124E9F7FD6F06792615A8");
        System.out.println("tx: " + transactionsInfo);
    }

    /**
     * 转账
     *
     * @param secret      秘钥
     * @param account     地址
     * @param destination 目标地址
     * @param amount      数量
     * @param memo        备注
     * @return 提交结果
     */
    public static PaymentResult payment(String secret, String account, String destination, String amount, String memo) {
        /*
         * 通过PRC接口获取当前账号的sequence
         */
        Integer sequence = rpcService.getSequence(account);
        /*
         * 组装参数
         */

        Map<String, Object> txMap = new HashMap<String, Object>();
        txMap.put("TransactionType", "Payment");
        txMap.put("Account", account);
        txMap.put("Destination", destination);
        txMap.put("Sequence", sequence);
        txMap.put("Amount", chainCrypto.buildAmount(amount, null, null));
        txMap.put("Fee", chainCrypto.baseToDrops(Const.BASE_FEE));
        txMap.put("Memos", chainCrypto.buildTextMemos(Const.MEMO_TYPE, memo));
        /*
         * 获取离线签名后的tx_blob
         */
        String txJson = JSONObject.toJSONString(txMap, true);
        SignedTransaction res = chainCrypto.sign(txJson, secret);
        /*
         * 将离线签名参数提交至RPC接口
         */
        String responseStr = rpcService.submit(res.tx_blob);
        JSONObject responseObj = JSONObject.parseObject(responseStr);
        /*
         * 解析RPC返回参数
         */
        PaymentResult paymentResult = new PaymentResult();
        paymentResult.setAccount(responseObj.getJSONObject("result").getJSONObject("tx_json").getString("Account"));
        paymentResult.setAmount(chainCrypto.dropsToBase(responseObj.getJSONObject("result").getJSONObject("tx_json").getString("Amount")));
        paymentResult.setDestination(responseObj.getJSONObject("result").getJSONObject("tx_json").getString("Destination"));
        paymentResult.setFee(chainCrypto.dropsToBase(responseObj.getJSONObject("result").getJSONObject("tx_json").getString("Fee")));
        paymentResult.setHash(responseObj.getJSONObject("result").getJSONObject("tx_json").getString("hash"));
        paymentResult.setTransactionType(responseObj.getJSONObject("result").getJSONObject("tx_json").getString("TransactionType"));
        JSONArray memoArr = responseObj.getJSONObject("result").getJSONObject("tx_json").getJSONArray("Memos");
        paymentResult.setMemo(chainCrypto.hexToString(memoArr.getJSONObject(0).getString("MemoData")));
        return paymentResult;
    }

    /**
     * 二级通证转账
     *
     * @param secret      秘钥
     * @param account     地址
     * @param destination 目标地址
     * @param currency    转账资产名称
     * @param value       转账数量
     * @param issuer      转账资产的发行地址
     * @param memo        备注
     * @return 提交结果
     */
    public static PaymentTokenResult paymentToken(String secret, String account, String destination, String currency, String value, String issuer, String memo) {
        /*
         * 通过PRC接口获取当前账号的sequence
         */
        Integer sequence = rpcService.getSequence(account);
        /*
         * 组装参数
         */
        Map<String, Object> txMap = new HashMap<>();
        txMap.put("TransactionType", "Payment");
        txMap.put("Account", account);
        txMap.put("Destination", destination);
        txMap.put("Sequence", sequence);
        txMap.put("Amount", chainCrypto.buildAmount(value, currency, issuer));
        txMap.put("Fee", chainCrypto.baseToDrops(Const.BASE_FEE));
        txMap.put("Memos", chainCrypto.buildTextMemos(Const.MEMO_TYPE, memo));
        /*
         * 获取离线签名后的tx_blob
         */
        String txJson = JSONObject.toJSONString(txMap, true);
        SignedTransaction res = chainCrypto.sign(txJson, secret);
        /*
         * 将离线签名参数提交至RPC接口
         */
        String responseStr = rpcService.submit(res.tx_blob);
        JSONObject responseObj = JSONObject.parseObject(responseStr);
        /*
         * 解析RPC返回参数
         */
        PaymentTokenResult paymentTokenResult = new PaymentTokenResult();
        paymentTokenResult.setAccount(responseObj.getJSONObject("result").getJSONObject("tx_json").getString("Account"));
        Amount amount = new Amount();
        amount.setCurrency(responseObj.getJSONObject("result").getJSONObject("tx_json").getJSONObject("Amount").getString("currency"));
        amount.setIssuer(responseObj.getJSONObject("result").getJSONObject("tx_json").getJSONObject("Amount").getString("issuer"));
        amount.setValue(responseObj.getJSONObject("result").getJSONObject("tx_json").getJSONObject("Amount").getString("value"));
        paymentTokenResult.setAmount(amount);
        paymentTokenResult.setDestination(responseObj.getJSONObject("result").getJSONObject("tx_json").getString("Destination"));
        paymentTokenResult.setFee(chainCrypto.dropsToBase(responseObj.getJSONObject("result").getJSONObject("tx_json").getString("Fee")));
        paymentTokenResult.setHash(responseObj.getJSONObject("result").getJSONObject("tx_json").getString("hash"));
        paymentTokenResult.setTransactionType(responseObj.getJSONObject("result").getJSONObject("tx_json").getString("TransactionType"));
        JSONArray memoArr = responseObj.getJSONObject("result").getJSONObject("tx_json").getJSONArray("Memos");
        paymentTokenResult.setMemo(chainCrypto.hexToString(memoArr.getJSONObject(0).getString("MemoData")));
        return paymentTokenResult;
    }

    /**
     * 生成新账号
     *
     * @return 账号地址和秘钥
     */
    public static AccountKeyPair generate() {
        AccountKeyPair result = new AccountKeyPair();
        KeyPair walletSE = chainCrypto.keyPair();
        result.setAccount(walletSE.getAddress());
        result.setSecret(walletSE.getSecret());
        return result;
    }

}
