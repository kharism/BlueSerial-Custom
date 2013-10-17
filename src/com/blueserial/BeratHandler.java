package com.blueserial;

public class BeratHandler extends AbstractHandler {
	private String berat;
	private String satuan;
	@Override
	public String Handle(String a) {
		// TODO Auto-generated method stub
		String[] p=a.split(" ");
		berat = p[7];
		satuan = String.valueOf(p[7].charAt(0));
		return berat;
	}

	@Override
	public String getSatuan(String a) {
		// TODO Auto-generated method stub
		String[] p=a.split(" ");
		berat = p[8];
		satuan = String.valueOf(p[8].charAt(0));
		return satuan;
	}

}
