package com.newsdistill.articleextractor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;


public class HTMLFetcherUtil {
  private static final Pattern PAT_CHARSET = Pattern.compile("charset=([^; ]+)$");

  public static Map<String, Object> getBytesFromURL(URL url) {
    Map<String, Object> resultMap = new HashMap<String, Object>();
    URLConnection conn = null;
    byte[] data = null;
    InputStream in = null;
    URI resourceUri = null;
    ByteArrayOutputStream bos = null;
    String referrer = null;
    try {
      conn = url.openConnection();

      resourceUri = new URI(url.toString());
      referrer = resourceUri.getHost();
      conn.setRequestProperty(ApplicationConstants.USER_AGENT, ApplicationConstants.USER_AGENT_VALUES);
      conn.addRequestProperty("REFERRER", referrer);
      conn.connect();
    } catch (URISyntaxException e) {
      e.printStackTrace();
    } catch (IOException ioe) {
      ioe.printStackTrace();

    }
    final String ct = conn.getContentType();

    try {
      if (ct == null || !(ct.equals("text/html") || ct.startsWith("text/html;"))) {
        throw new IOException("Unsupported content type: " + ct);
      }

      Charset cs = Charset.forName("Cp1252");
      if (ct != null) {
        Matcher m = PAT_CHARSET.matcher(ct);
        if (m.find()) {
          final String charset = m.group(1);
          try {
            cs = Charset.forName(charset);
            resultMap.put("charset", cs);
          } catch (UnsupportedCharsetException e) {
            // keep default
            e.printStackTrace();
          }
        } else if (ct.contains(cs.toString())) {
          resultMap.put("charset", cs);
        }else{
          resultMap.put("charset", cs); 
        }
      }

      in = conn.getInputStream();

      final String encoding = conn.getContentEncoding();
      if (encoding != null) {
        if ("gzip".equalsIgnoreCase(encoding)) {
          in = new GZIPInputStream(in);
        } else {
          System.err.println("WARN: unsupported Content-Encoding: " + encoding);
        }
      }
      bos = new ByteArrayOutputStream();
      byte[] buf = new byte[4096];
      int r;
      while ((r = in.read(buf)) != -1) {
        bos.write(buf, 0, r);
      }
      data = bos.toByteArray();
      in.close();
      bos.close();
    } catch (IOException ioe) {
      ioe.printStackTrace();

    }
    resultMap.put("bytes", data);
    return resultMap;
  }

}
