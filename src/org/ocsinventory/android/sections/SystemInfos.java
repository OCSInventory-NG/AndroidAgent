package org.ocsinventory.android.sections;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ocsinventory.android.actions.OCSLog;

public class SystemInfos {
	
	private final static String CPUFREQPATH = "/sys/devices/system/cpu/cpu0/cpufreq/scaling_max_freq";
	private final static String CPUINFOPATH = "/proc/cpuinfo";
	private final static String MEMINFOPATH = "/proc/meminfo";
	
	private static SystemInfos instance;

	private static int processorNumber;
	private static String processorName;
	private static String processorType;
	private static int processorSpeed;

	private static String serial;
	
	private static int memtotal;
	private static int swaptotal;
	
	private static OCSLog ocslog;	

	public static SystemInfos getInstance() {
		if (instance == null)
			instance = new SystemInfos();
		return instance;
	}
	
	public static void InitSystemInfos() {
		ocslog = OCSLog.getInstance();
		ocslog.append("SYSTEMINFOS start");
		readCpuinfo();
		readMeminfo();
		readCpuFreq();
		ocslog.append("SYSTEMINFOS end");
	}

	private static void readCpuinfo() {
		try {
			File f = new File(CPUINFOPATH);
			BufferedReader bReader = new BufferedReader(new FileReader(f), 8192);
			String line;
			int nbProc=0;
			while ( ( line =  bReader.readLine() ) != null  ) {
				Pattern p = Pattern.compile(".*processor.*:(.*)", Pattern.CASE_INSENSITIVE);
				Matcher m= p.matcher(line);
				if ( m.find() ) {
					processorName=m.group(1).trim();
					nbProc++;
				}
				p = Pattern.compile(".*architecture.*:(.*)\\s.*", Pattern.CASE_INSENSITIVE);
				m= p.matcher(line);
				if ( m.find() ) {
					processorType=m.group(1).trim();
				}
				p = Pattern.compile(".*serial.*:(.*)\\s.*", Pattern.CASE_INSENSITIVE);
				m= p.matcher(line);
				if ( m.find() ) {
					serial=m.group(1);
				}
			}
			processorNumber=nbProc;
			bReader.close();		}
		catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	} 
	
	private static void readMeminfo() {
		try {
			File f = new File(MEMINFOPATH);
			BufferedReader bReader = new BufferedReader(new FileReader(f), 8192);
			String line;
			while ( ( line =  bReader.readLine() ) != null  ) {
				Pattern p = Pattern.compile(".*memtotal.*:(.*)\\s.*", Pattern.CASE_INSENSITIVE);
				Matcher m= p.matcher(line);
				if ( m.find() ) {
						memtotal=Integer.parseInt(m.group(1).trim());
				}
				p=Pattern.compile(".*swaptotal.*:(.*)\\s.*", Pattern.CASE_INSENSITIVE);
				m= p.matcher(line);
				if ( m.find() ) {
						swaptotal=Integer.parseInt(m.group(1).trim());
				}
			}
			bReader.close();
		} catch (FileNotFoundException e) {
			ocslog.append("File not found : "+MEMINFOPATH);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	public static void readCpuFreq() {
		File f = new File(CPUFREQPATH);
		try {
            BufferedReader bReader = new BufferedReader(new FileReader(f), 8192);
            String line = bReader.readLine();
            processorSpeed = Integer.parseInt(line);
            bReader.close();
        } catch (FileNotFoundException e) {
			ocslog.append("File not found : "+CPUFREQPATH);
        } catch (IOException e) {
        	ocslog.append("IO error reading "+CPUFREQPATH);
        }
    }

	public static int getProcessorNumber() {
		return processorNumber;
	}

	public static String getProcessorName() {
		return processorName;
	}

	public String getProcessorType() {
		return processorType;
	}

	public String getSerial() {
		return serial;
	}

	public static int getMemtotal() {
		return memtotal;
	}

	public static int getSwaptotal() {
		return swaptotal;
	}
	public static int getProcessorSpeed() {
		return processorSpeed;
	}
}
