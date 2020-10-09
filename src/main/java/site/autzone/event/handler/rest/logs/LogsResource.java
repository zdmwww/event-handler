package site.autzone.event.handler.rest.logs;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class LogsResource {
	private final static String V1BASE = "v1";
	
	@GetMapping(V1BASE+"/logs/tail")
	public List<String> getLogs(@RequestParam("filePath") String filePath) {
		List<String> result = new ArrayList<String>();
		String fileAbPath = filePath;
		File file = new File(fileAbPath);
		RandomAccessFile rf = null;
		try {
			rf = new RandomAccessFile(file.getAbsolutePath(), "r");
			long len = rf.length();
			long start = rf.getFilePointer();
			long nextend = start + len - 1;
			String line;
			rf.seek(nextend);
			int c = -1;
			while (nextend > start) {
				c = rf.read();
				if (c == '\n' || c == '\r') {
					line = rf.readLine();
					filterLine(result, line);
					if(result.size() >= 1000) return result;
					nextend--;
				}
				nextend--;
				rf.seek(nextend);
				if (nextend == 0) {
					line = rf.readLine();
					filterLine(result, line);
					if(result.size() >= 1000) return result;
				}
			}
		}catch(Exception e) {
			if(rf != null) {
				try {
					rf.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}finally {
			if(rf != null) {
				try {
					rf.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return result;
	}
	
	private void filterLine(List<String> lst, String line) throws UnsupportedEncodingException {
		if(line != null) {
			line = new String(line .getBytes("ISO-8859-1"), "UTF-8");
			lst.add(line);
		}
	}
}
