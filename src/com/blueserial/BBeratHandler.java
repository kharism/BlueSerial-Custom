package com.blueserial;

public class BBeratHandler extends AbstractHandler {
	String data;
	String satuan;
	@Override
	public String Handle(String a) {
		// TODO Auto-generated method stub
		try{String[] p = a.split(" ");
		data = p[2];
		satuan = p[3];
		double dataKg = Double.parseDouble(data)/1000;
		return String.valueOf(dataKg);}
		catch(Exception ex){
			return "";
		}
	}

	@Override
	public String getSatuan(String a) {
		// TODO Auto-generated method stub
		String[] p = a.split(" ");
		data = p[1];
		satuan = p[2];
		//double dataKg = Double.parseDouble(data);
		return "Kg";
	}

}
