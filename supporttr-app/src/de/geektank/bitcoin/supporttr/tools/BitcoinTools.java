package de.geektank.bitcoin.supporttr.tools;

import java.util.ArrayList;

import org.bitcoin.protocols.payments.Protos;
import org.bitcoin.protocols.payments.Protos.PaymentRequest;

import com.google.bitcoin.core.Address;
import com.google.bitcoin.core.AddressFormatException;
import com.google.bitcoin.core.NetworkParameters;
import com.google.bitcoin.params.MainNetParams;
import com.google.bitcoin.params.TestNet3Params;
import com.google.bitcoin.script.ScriptBuilder;
import com.google.protobuf.ByteString;

import de.geektank.bitcoin.supporttr.data.PayoutTransaction;

public class BitcoinTools {
	
	public static final boolean TESTNET3 	= true;
	public static final boolean PRODUCTION 	= false;
	
	public static String stripBitcoinAddress(String value) {
		if (value==null) return null;
		String result = value;
		if (result.indexOf(":")>=0) result = result.substring(result.indexOf(":")+1);
		if (result.indexOf("?")>0) result = result.substring(0,result.indexOf("?"));
		return result;
	}
	
	public static boolean validateBitcoinAddress(String btc, boolean onTestNet) {
		
		if (btc==null) return false;
		
		try {
			NetworkParameters params;
			if (onTestNet) {
				params = new TestNet3Params();
			} else {
				params = new MainNetParams();
			}
			Address bitcoinAddress = new Address(params, btc);
			if (bitcoinAddress!=null) return true;
		} catch (AddressFormatException e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	public static byte[] generatePaymentRequest(ArrayList<PayoutTransaction> list, boolean onTestnet) {
		
		try{
	
			NetworkParameters params = null;
			if (onTestnet) {
				params = new TestNet3Params();
			} else {
				params = new MainNetParams();
			}

			final Protos.PaymentDetails.Builder paymentDetails = Protos.PaymentDetails.newBuilder();
			paymentDetails.setNetwork(params.getPaymentProtocolId());
			
			// multiple outputs
			for (PayoutTransaction payoutTransaction : list) {
				final Protos.Output.Builder output = Protos.Output.newBuilder();
				output.setAmount(BitcoinTools.double2NanoLong(payoutTransaction.amount));
				output.setScript(ByteString.copyFrom(ScriptBuilder.createOutputScript(new Address(params , payoutTransaction.addr)).getProgram()));
				paymentDetails.addOutputs(output);
			}
			
			paymentDetails.setMemo("Supporttr Donations");
			paymentDetails.setTime(System.currentTimeMillis());
			
			final Protos.PaymentRequest.Builder paymentRequest = Protos.PaymentRequest.newBuilder();
			paymentRequest.setSerializedPaymentDetails(paymentDetails.build().toByteString());
			
			PaymentRequest pr = paymentRequest.build();
			return pr.toByteArray();
			
		}
		catch (Exception x)
		{
			throw new RuntimeException(x);
		}
			
	}

	public static long double2NanoLong(double amount) {
		return Math.round((amount * Long.parseLong("100000000")));
	}
	
	public static String double2mBTC(double amount) {
		return "TODO:"+amount+"BTC";
	}
}
