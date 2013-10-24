package com.blueserial;

public class BIHandler extends AbstractHandler {

	@Override
	public String Handle(String a) {
		// TODO Auto-generated method stub
		try{
			String[] p = a.split(" ");
			if(Double.parseDouble(p[2])!=0)
				return p[2];
			else
				return "";
		}
		catch(Exception ex){
			String mes = ex.getMessage();
			return "";
		}
	}

	@Override
	public String getSatuan(String a) {
		// TODO Auto-generated method stub
		return "Kg";
	}

}
