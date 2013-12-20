package com.blueserial;
/**
 * 
 * @author kharisma
 *
 */
public class LilaLikaHandler extends AbstractHandler{
	private String berat;
	private String satuan;

	@Override
	public String Handle(String a) {
		// TODO Auto-generated method stub
				try{
					String[] p = a.split("(\\s)+");
					if(Double.parseDouble(p[p.length-2])!=0){
						double h = Double.parseDouble(p[p.length-2]);
						h = h+7.0;
						return String.valueOf(h);
					}
						
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
		return "mm";
	}

}
