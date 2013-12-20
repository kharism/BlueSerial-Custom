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
		try{String[] p = a.split("(\\s)+");
		nilai = p[1];
		double h = Double.parseDouble(nilai);
		Satuan = p[2];
		String jj = String.valueOf(h); 
		return jj;}
		catch(Exception ex){
			return "";
		}
	}

	@Override
	public String getSatuan(String a) {
		// TODO Auto-generated method stub
		try{
		String[] p = a.split(" ");
		nilai = p[3];
		Satuan = p[4];
		return Satuan;}
		catch(Exception ex){
			return "cm";
		}
	}

}
