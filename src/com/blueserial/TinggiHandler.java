package com.blueserial;

public class TinggiHandler extends AbstractHandler {
	String nilai;
	String Satuan;
	@Override
	/**
	 * return nilai dari hasil bacaan sensor
	 */
	public String Handle(String a) {
		// TODO Auto-generated method stub
		String[] p = a.split(" ");
		nilai = p[3];
		Satuan = p[4];
		return nilai;
	}

	@Override
	public String getSatuan(String a) {
		// TODO Auto-generated method stub
		String[] p = a.split(" ");
		nilai = p[3];
		Satuan = p[4];
		return Satuan;
	}

}
