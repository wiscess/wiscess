package com.wiscess.web;

import java.awt.image.BufferedImage;

import com.google.code.kaptcha.impl.DefaultNoise;

public class CaptchaNoNoise extends DefaultNoise{

	public void makeNoise(BufferedImage image, float factorOne,
			float factorTwo, float factorThree, float factorFour) {
		//nothing
	}
}
