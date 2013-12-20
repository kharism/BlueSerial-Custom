package com.blueserial;

public class BIHandler extends AbstractHandler {

	@Override
	public String Handle(String a) {
		try{
			String[] p = a.split("(\\s)+");
			String[] q = p[1].split("\\.");
			if(Double.parseDouble(q[1]+"."+q[2])!=0)
				return q[1]+"."+q[2];
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
