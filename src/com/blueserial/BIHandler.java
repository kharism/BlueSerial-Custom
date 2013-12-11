package com.blueserial;

public class BIHandler extends AbstractHandler {

	@Override
	public String Handle(String a) {
		try{
			String[] p = a.split("(\\s)+");
			if(Double.parseDouble(p[1])!=0)
				return p[1];
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
