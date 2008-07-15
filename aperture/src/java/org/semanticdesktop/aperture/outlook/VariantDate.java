/*
 * Copyright (c) 2005 - 2008 Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.outlook;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 *  copied from http://discuss.microsoft.com/SCRIPTS/WA-MSD.EXE?A2=ind9909E&L=Java-COM&P=R337
 */
public class VariantDate
{
  public VariantDate(double value){ mValue = value; }
  public VariantDate(){
	set( new java.util.GregorianCalendar());
  }
  public VariantDate(java.util.GregorianCalendar cal){
	set(cal);
  }
  public VariantDate(java.util.Date dt){
	setDate(dt);
  }
  public void set(GregorianCalendar cal){
set(cal.get(Calendar.YEAR),cal.get(Calendar.MONTH)+1,cal.get(Calendar.DATE),cal.get(Calendar.HOUR_OF_DAY),cal.get(Calendar.MINUTE),cal.get(Calendar.SECOND),cal.get(Calendar.MILLISECOND));
  }
  public synchronized String toString(){
	java.text.DateFormat f = java.text.DateFormat.getDateTimeInstance(java.text.DateFormat.MEDIUM,java.text.DateFormat.MEDIUM);
	return f.format(get().getTime());
  }
  public synchronized double getValue(){ return mValue; }
  public synchronized void setValue(double newValue){ mValue = newValue; }
  public java.util.Date  getDate(){
	java.util.GregorianCalendar cal = get();
	return new java.util.Date(cal.getTime().getTime());
  }
  public java.util.Date getDate(java.util.Calendar cal){
	initCalendar(cal);
	return new java.util.Date(cal.getTime().getTime());
  }
  public synchronized void setDate(java.util.Date dt){
	java.util.GregorianCalendar cal = new java.util.GregorianCalendar();
	cal.setTime(dt);
	set(cal);
  }
  public synchronized void set(int y,int m,int d,int h,int i,int s,int l){
	if(m > 2) m -= 3;
	else { m += 9; y--; }
	int c = y / 100, ya = y - 100*c;
	int vDate = ((146097*c)>>2) + ((1461*ya)>>2) + (153*m + 2)/5 + d -
693900;
	mValue = h*3600000 + i*60000 + s*1000 + l;
	mValue /= 86400000.;
	mValue += vDate;
  }
  public java.util.GregorianCalendar get(){
	java.util.GregorianCalendar cal = new java.util.GregorianCalendar();
	initCalendar(cal);
	return cal;
  }
  public synchronized void initCalendar(java.util.Calendar cal){
	int y,d,m,h,i,s,l;
	int j = (int)mValue;
	double r = Math.round((mValue - j) * 86400000.);
	j += 693900;
	y = ((j<<2) - 1) / 146097;
	j = (j<<2) - 1 - 146097*y;
	d = (j>>2);
	j = ((d<<2) + 3) / 1461;
	d = (d<<2) + 3 - 1461*j;
	d = (d + 4)>>2;
	m = (5*d - 3)/153;
	d = 5*d - 3 - 153*m;
	d = (d + 5)/5;
	y = (100*y + j);
	if (m < 10) m += 3;
	else { m -= 9; y++; }
	h = (int) r / 3600000;
	r -= h*3600000;
	i = (int) r / 60000;
	r -= i*60000;
	s = (int) r / 1000;
	l = (int) r - s*1000;
	cal.set(y,m-1,d,h,i,s);
	cal.set(Calendar.MILLISECOND,l);
  }
  private double mValue;
}


/*
 * $Log$
 * Revision 1.3  2006/10/20 17:43:00  mylka
 * removed the umlauts from the copyright message.
 *
 * Revision 1.2  2006/02/27 14:36:30  leo_sauermann
 * corrected license: (C) DFKI, OSL 3.0
 *
 * Revision 1.1  2006/02/27 14:05:48  leo_sauermann
 * Implemented First version of Outlook. Added the vocabularyWriter for ease of vocabulary and some launch configs to run it. Added new dependencies (jacob)
 *
 * Revision 1.1  2005/01/13 13:16:04  sauermann
 * project restructuring
 *
 * Revision 1.1  2004/11/22 14:43:47  sauermann
 * init
 *
 * Revision 1.4  2004/09/09 15:38:32  kiesel
 * - added CVS tags
 *
 */
