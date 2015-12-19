package com.kohlschutter.boilerpipe.demo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;
import org.xml.sax.SAXException;

import com.kohlschutter.boilerpipe.BoilerpipeProcessingException;
import com.kohlschutter.boilerpipe.demo.utils.CommonUtils;
import com.kohlschutter.boilerpipe.extractors.ArticleExtractor;
import com.kohlschutter.boilerpipe.extractors.CommonExtractors;
import com.kohlschutter.boilerpipe.sax.HTMLHighlighter;

public class PreprocessedPageBoilerPipe {
	private static final Pattern PAT_CHARSET = Pattern
			.compile("charset=([^; ]+)$");

	public final static String regexForIframeStart = "(?i)(<[\\s]*iframe[\\s]*[^>]*>)";
	static Pattern patternForIframe = Pattern.compile(regexForIframeStart);

	public static void main(String[] args) throws IOException,
			BoilerpipeProcessingException, SAXException {

		// boiler pipe doesn't keep iframes it beahves irregularly
		Map<String, Object> getBytesFromUrl = getBytesFromUrl("http://telugutips.in/what-are-the-healthy-habits-that-make-you-more-handsome/");

		Map<String, String> ifameVideoRetentionMap = new LinkedHashMap<String, String>();
		byte[] result = (byte[]) getBytesFromUrl.get("bytes");
		Charset cs = (Charset) getBytesFromUrl.get("charset");

		cs = (cs == null) ? Charset.forName("UTF-8") : cs;

		Document doc = Jsoup.parse(new String(result));

		doc.select("iframe,IFRME").select("html").remove();
		doc.select("script").remove();
		doc.select("noscript,NOSCRIPT").remove();
		//doc.select("embed").remove();
		Elements elements = doc.select("iframe,IFRAME").clone();
		if (!elements.isEmpty()) {
			String dataFromElement = null;
			for (Element element : elements) {

				dataFromElement = element.toString();
				Matcher matcherForIframe = patternForIframe
						.matcher(dataFromElement);
				if (matcherForIframe.find()) {
					int startIndex = matcherForIframe.start();
					int endIndex = matcherForIframe.end();
					String IframeKey = dataFromElement.substring(startIndex,
							endIndex);
					if (!ifameVideoRetentionMap.containsKey(IframeKey))
						ifameVideoRetentionMap.put(IframeKey, dataFromElement);
				}
			}
		}
		System.out.println(doc.toString());
		result = doc.toString().getBytes();
		ArticleExtractor ce = null;
		ce = CommonExtractors.ARTICLE_EXTRACTOR;
		final HTMLHighlighter contentHighlighter = HTMLHighlighter
				.newHighlightingInstance();
		String opFromBoilerPipe = contentHighlighter.process(ce, result, cs);
		String IframeRetention = replaceIframecontentWithOriginalContent(
				ifameVideoRetentionMap, opFromBoilerPipe);
		System.out
				.println("--------------------------------------------------------------------");
		System.out.println(IframeRetention);

	}

	private static String replaceIframecontentWithOriginalContent(
			Map<String, String> iframeDataMap, String htmlFromBoilerPipe) {
		// boiler pipe doesn't actually
		String replacableValue = CommonUtils.getStringMatchedForGivenPattern(
				regexForIframeStart, htmlFromBoilerPipe);

		if (!StringUtils.isBlank(replacableValue))
			
		{
			replacableValue = replacableValue.replaceAll("IFRMAE", "iframe");
		String replacedvalue = iframeDataMap.get(replacableValue);

		htmlFromBoilerPipe = htmlFromBoilerPipe.replaceFirst(replacableValue,
				replacedvalue);
		}
		return htmlFromBoilerPipe;
	}

	public static Map<String, Object> getBytesFromUrl(String urlString) {

		ByteArrayOutputStream bos = null;
		InputStream in = null;
		URLConnection conn = null;
		String ct = null;
		URL url = null;
		Map<String, Object> resultMap = new LinkedHashMap<String, Object>();
		if (StringUtils.isNotBlank(urlString)
				&& urlString.matches("^http(s)?://.*")) {

			try {
				url = new URL(urlString);
				conn = url.openConnection();
				ct = conn.getContentType();
				if (ct == null
						|| !(ct.equals("text/html") || ct
								.startsWith("text/html;"))) {
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
						}
					}
				}
				in = conn.getInputStream();

				bos = new ByteArrayOutputStream();
				byte[] buf = new byte[4096];
				int r;
				while ((r = in.read(buf)) != -1) {
					bos.write(buf, 0, r);
				}
				in.close();

				final String encoding = conn.getContentEncoding();
				if (encoding != null) {
					if ("gzip".equalsIgnoreCase(encoding)) {
						in = new GZIPInputStream(in);
					} else {
						System.err
								.println("WARN: unsupported Content-Encoding: "
										+ encoding);
					}
				}
			} catch (MalformedURLException e) {

				e.printStackTrace();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}

			final byte[] data = bos.toByteArray();
			resultMap.put("bytes", data);
			return resultMap;

		} else {
			return null;
		}

	}

}
