package com.newsdistill.articleextractor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import org.apache.commons.lang3.StringUtils;


public class HTMLFetcherUtil {
  private static final Pattern PAT_CHARSET = Pattern.compile("charset=([^; ]+)$");

  public static Map<String, Object> getBytesFromURL(URL url) {
    Map<String, Object> resultMap = new HashMap<String, Object>();
    URLConnection conn = null;
    byte[] data = null;
    InputStream in = null;
    ByteArrayOutputStream bos = null;
    String referrer = null;
    try {
      url = new URL(buildItemUrl(url));
      conn = url.openConnection();
      String protocol = url.getProtocol();
      referrer = url.getHost();
      conn.setRequestProperty(ApplicationConstants.USER_AGENT, ApplicationConstants.USER_AGENT_VALUES);
      conn.addRequestProperty("REFERRER", protocol + "://" + referrer);
      conn.setReadTimeout(ApplicationConstants.READ_TIME_OUT);
      conn.connect();
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
        } else {
          resultMap.put("charset", Charset.forName("UTF-8"));
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

  public static String buildItemUrl(URL resourceUri) throws UnsupportedEncodingException {
    StringBuilder sbr = new StringBuilder();
    String protocol = resourceUri.getProtocol();
    String path = resourceUri.getPath();
    String queryParams = resourceUri.getQuery();
    System.out.println(queryParams);
    String reference = resourceUri.getRef();
    sbr.append(protocol);
    sbr.append("://");
    sbr.append(resourceUri.getHost());
    if (StringUtils.isBlank(path)) {
      sbr.toString();
    }
    path = URLEncoder.encode(path, "UTF-8").replaceAll("%2F", "/").replaceAll("%3D","=");
    sbr.append(path);
    if (StringUtils.isBlank(queryParams)) {
      return sbr.toString();
    }
    sbr.append("?");
    queryParams=URLEncoder.encode(queryParams, "UTF-8").replaceAll("%2F", "/").replaceAll("%3D","=").replaceAll("%26","?");
    System.out.println(queryParams);
    sbr.append(queryParams);
    if (StringUtils.isBlank(reference)) {
      return sbr.toString();
    }
    sbr.append("#");
    sbr.append(reference);
    return sbr.toString();
  }

}
