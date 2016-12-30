package org.rr.mobi4java.util;


import static org.junit.Assert.assertEquals;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import org.junit.Test;

public class MobiLz77Test {
	
	/** url encoded example string for testing */
	private static final String EXAMPLE_STRING = "Sanskrit%3A%20%E0%A4%95%E0%A4%BE%E0%A4%9A%E0%A4%82%20%E0%A4%B6%E0%A4%95%E0%A5%8D%E0%A4%A8%E0%A5%8B%E0%A4%AE%E0%A5%8D%E0%A4%AF%E0%A4%A4%E0%A5%8D%E0%A4%A4%E0%A5%81%E0%A4%AE%E0%A5%8D%20%E0%A5%A4%20%E0%A4%A8%E0%A5%8B%E0%A4%AA%E0%A4%B9%E0%A4%BF%E0%A4%A8%E0%A4%B8%E0%A5%8D%E0%A4%A4%E0%A4%BF%20%E0%A4%AE%E0%A4%BE%E0%A4%AE%E0%A5%8D%20%E0%A5%A5%20Sanskrit%20(standard%20transcription)%3A%20k%C4%81ca%E1%B9%83%20%C5%9Baknomyattum%3B%20nopahinasti%20m%C4%81m.%20Classical%20Greek%3A%20%E1%BD%95%CE%B1%CE%BB%CE%BF%CE%BD%20%CF%95%CE%B1%CE%B3%CE%B5%E1%BF%96%CE%BD%20%CE%B4%E1%BD%BB%CE%BD%CE%B1%CE%BC%CE%B1%CE%B9%CE%87%20%CF%84%CE%BF%E1%BF%A6%CF%84%CE%BF%20%CE%BF%E1%BD%94%20%CE%BC%CE%B5%20%CE%B2%CE%BB%E1%BD%B1%CF%80%CF%84%CE%B5%CE%B9.%20Greek%20(monotonic)%3A%20%CE%9C%CF%80%CE%BF%CF%81%CF%8E%20%CE%BD%CE%B1%20%CF%86%CE%AC%CF%89%20%CF%83%CF%80%CE%B1%CF%83%CE%BC%CE%AD%CE%BD%CE%B1%20%CE%B3%CF%85%CE%B1%CE%BB%CE%B9%CE%AC%20%CF%87%CF%89%CF%81%CE%AF%CF%82%20%CE%BD%CE%B1%20%CF%80%CE%AC%CE%B8%CF%89%20%CF%84%CE%AF%CF%80%CE%BF%CF%84%CE%B1.%20Greek%20(polytonic)%3A%20%CE%9C%CF%80%CE%BF%CF%81%E1%BF%B6%20%CE%BD%E1%BD%B0%20%CF%86%CE%AC%CF%89%20%CF%83%CF%80%CE%B1%CF%83%CE%BC%CE%AD%CE%BD%CE%B1%20%CE%B3%CF%85%CE%B1%CE%BB%CE%B9%E1%BD%B0%20%CF%87%CF%89%CF%81%E1%BD%B6%CF%82%20%CE%BD%E1%BD%B0%20%CF%80%CE%AC%CE%B8%CF%89%20%CF%84%CE%AF%CF%80%CE%BF%CF%84%CE%B1.%20%20Etruscan%3A%20(NEEDED)%20Latin%3A%20Vitrum%20edere%20possum%3B%20mihi%20non%20nocet.%20Old%20French%3A%20Je%20puis%20mangier%20del%20voirre.%20Ne%20me%20nuit.%20French%3A%20Je%20peux%20manger%20du%20verre%2C%20%C3%A7a%20ne%20me%20fait%20pas%20mal.%20Proven%C3%A7al%20%2F%20Occitan%3A%20P%C3%B2di%20manjar%20de%20veire%2C%20me%20nafrari%C3%A1%20pas.%20Qu%C3%A9b%C3%A9cois%3A%20J%27peux%20manger%20d%27la%20vitre%2C%20%C3%A7a%20m%27fa%20pas%20mal.%20Walloon%3A%20Dji%20pou%20magn%C3%AE%20do%20v%C3%AAre%2C%20%C3%A7oula%20m%27%20freut%20n%C3%A9n%20m%C3%A5.%20%20Champenois%3A%20(NEEDED)%20%20Lorrain%3A%20(NEEDED)%20Picard%3A%20Ch%27peux%20mingi%20du%20verre%2C%20cha%20m%27fo%C3%A9%20mie%20n%27ma.%20%20Corsican%2FCorsu%3A%20(NEEDED)%20%20J%C3%A8rriais%3A%20(NEEDED)%20Krey%C3%B2l%20Ayisyen%20(Hait%C3%AF)%3A%20Mwen%20kap%20manje%20v%C3%A8%2C%20li%20pa%20blese%27m.";

	@Test
	public void testEncodeDecode() throws UnsupportedEncodingException {
		String example = URLDecoder.decode(EXAMPLE_STRING, "UTF-8");
		byte[] encoded = MobiLz77.lz77Encode(example.getBytes());

		String reEncoded = MobiLz77.lz77DecodeToString(encoded, StandardCharsets.UTF_8.toString());
		assertEquals(example, reEncoded);
	}
}
