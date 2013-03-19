package org.ocsinventory.android.sections;

import java.io.File;

import org.ocsinventory.android.actions.Utils;

import android.os.StatFs;
import android.text.format.DateFormat;

public class OCSDrive {

	private String createdate;
	private String filesystem;
	private String type;
	private long free;
	private String label;
	private String serial;
	private long total;
	private String volumName;

	public OCSDrive(String fs) {
		File d = new File (fs);
		if ( d == null )
			return;
		StatFs statfs = new StatFs(fs);
		long bs = statfs.getBlockSize();
		long bc = statfs.getBlockCount();
		long fb = statfs.getFreeBlocks();
 
		this.volumName = null;
		this.label=null;
		this.total = bs * bc / 1048576L;
		this.free = bs * fb / 1048576L;
		this.type = fs;
		this.filesystem = null;
		this.createdate = (String) DateFormat.format("MM/dd/yy mm:ss", d.lastModified());
	}
	/*
	public OCSDrive(String type) {
		this.type = type;
	}
	*/
	/*
	 * <CREATEDATE>2011/11/17 20:47:06</CREATEDATE>
	 * <FILESYSTEM>ext4</FILESYSTEM> <FREE>29584</FREE> <LABEL/>
	 * <SERIAL>e340fd1a-108f-4523-a4f5-25d1c09bb0f2</SERIAL>
	 * <TOTAL>37547</TOTAL> <TYPE>/</TYPE> <VOLUMN>/dev/sda2</VOLUMN>
	 */
	public OCSSection getSection() {
		OCSSection s = new OCSSection("DRIVES");
		s.setAttr("CREATEDATE", createdate);
		s.setAttr("FILESYSTEM", filesystem);
		s.setAttr("TYPE", type);
		s.setAttr("FREE", String.valueOf(free));
		s.setAttr("LABEL", label);
		s.setAttr("SERIAL", null);
		s.setAttr("TOTAL", String.valueOf(total));
		s.setAttr("VOLUMN", volumName);
		s.setTitle(volumName);
		return s;
	}
	public String toXml() {
		return getSection().toXML();
	}
	public String toString() {
		return getSection().toString();
	}
	public String toXML() {
		return getSection().toXML();
	}
	public String getCreatedate() {
		return createdate;
	}
	public void setCreatedate(String createdate) {
		this.createdate = createdate;
	}
	public String getFilesystem() {
		return filesystem;
	}
	public void setFilesystem(String filesystem) {
		this.filesystem = filesystem;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public long getFree() {
		return free;
	}
	public void setFree(long free) {
		this.free = free;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getSerial() {
		return serial;
	}
	public void setSerial(String serial) {
		this.serial = serial;
	}
	public long getTotal() {
		return total;
	}
	public void setTotal(long total) {
		this.total = total;
	}
	public String getVolumName() {
		return volumName;
	}
	public void setVolumName(String volumName) {
		this.volumName = volumName;
	}
}