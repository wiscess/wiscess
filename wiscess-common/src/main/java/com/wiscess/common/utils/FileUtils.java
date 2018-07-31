package com.wiscess.common.utils;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FileUtils {
	/**
	   * Send file.
	   *
	   * @param request the request
	   * @param response the response
	   * @param file the file
	   * @throws IOException Signals that an I/O exception has occurred.
	   */
	  public static void sendFile(HttpServletRequest request, HttpServletResponse response, File file)
	      throws IOException {

	    try (OutputStream out = response.getOutputStream();
	        RandomAccessFile raf = new RandomAccessFile(file, "r")) {
	      long fileSize = raf.length();
	      long rangeStart = 0;
	      long rangeFinish = fileSize - 1;

	      // accept attempts to resume download (if any)
	      String range = request.getHeader("Range");
	      if (range != null && range.startsWith("bytes=")) {
	        String pureRange = range.replaceAll("bytes=", "");
	        int rangeSep = pureRange.indexOf('-');

	        try {
	          rangeStart = Long.parseLong(pureRange.substring(0, rangeSep));
	          if (rangeStart > fileSize || rangeStart < 0) {
	            rangeStart = 0;
	          }
	        } catch (NumberFormatException e) {
	          // keep rangeStart unchanged
	          log.trace("", e);
	        }

	        if (rangeSep < pureRange.length() - 1) {
	          try {
	            rangeFinish = Long.parseLong(pureRange.substring(rangeSep + 1));
	            if (rangeFinish < 0 || rangeFinish >= fileSize) {
	              rangeFinish = fileSize - 1;
	            }
	          } catch (NumberFormatException e) {
	            log.trace("", e);
	          }
	        }
	      }

	      // set some headers
	      response.setContentType("application/x-download");
	      response.setHeader("Content-Disposition", "attachment; filename=" + file.getName());
	      response.setHeader("Accept-Ranges", "bytes");
	      response.setHeader("Content-Length", Long.toString(rangeFinish - rangeStart + 1));
	      response.setHeader("Content-Range",
	          "bytes " + rangeStart + "-" + rangeFinish + "/" + fileSize);

	      // seek to the requested offset
	      raf.seek(rangeStart);

	      // send the file
	      byte[] buffer = new byte[4096];

	      long len;
	      int totalRead = 0;
	      boolean nomore = false;
	      while (true) {
	        len = raf.read(buffer);
	        if (len > 0 && totalRead + len > rangeFinish - rangeStart + 1) {
	          // read more then required?
	          // adjust the length
	          len = rangeFinish - rangeStart + 1 - totalRead;
	          nomore = true;
	        }

	        if (len > 0) {
	          out.write(buffer, 0, (int) len);
	          totalRead += len;
	          if (nomore) {
	            break;
	          }
	        } else {
	          break;
	        }
	      }
	    }
	  }
}
