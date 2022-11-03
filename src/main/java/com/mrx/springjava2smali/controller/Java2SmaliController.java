package com.mrx.springjava2smali.controller;

import com.mrx.springjava2smali.util.Java2SmaliUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author Mr.X
 * @since 2022-11-03 20:21
 */
@RestController
public class Java2SmaliController {

    private static final Logger logger = LoggerFactory.getLogger(Java2SmaliController.class);

    @PostMapping("/java2smali")
    public String java2Smali(@RequestParam String clazzName, @RequestBody MultipartFile file) throws IOException {
        String code = new String(file.getBytes());
        logger.debug("java2smali, className: {}, codeLength: {}", clazzName, code.length());
        return Java2SmaliUtils.java2Smali(clazzName, code);
    }

}
