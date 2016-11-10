package com.example;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class CameraPageGenerator {
	
	public static void main(String [] args) {
		CameraPageGenerator cpg = CameraPageGenerator.getInstance();
		cpg.createPage(233);
	}
	
	
	private static CameraPageGenerator instance = null;
	private final String srcPageName = FileUtil.getProjectPath()+"/src/main/webapp/index.html";
	
	private CameraPageGenerator() {

	}
	
	public static synchronized CameraPageGenerator getInstance() {
		if(instance == null) {
			instance = new CameraPageGenerator();
		}
		return instance;
	}
	
	public void createPage(int camId) {
		String name = FileUtil.getProjectPath()+"/src/main/webapp/" + camId + ".html";
		File src = new File(srcPageName);
		File target = new File(name);
		FileInputStream fi = null;
		FileOutputStream fo = null;
		FileChannel in = null;
		FileChannel out = null;

		try {
			fi = new FileInputStream(src);
			fo = new FileOutputStream(target);
			in = fi.getChannel();
			out = fo.getChannel();
			in.transferTo(0, in.size(), out);

		} catch (IOException e) {
			e.printStackTrace();
		} finally {

			try {
				fi.close();
				in.close();
				fo.close();
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
}
