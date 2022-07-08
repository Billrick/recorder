package com.rick.controller;

import com.rick.base.controller.BaseController;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v")
@RequiredArgsConstructor
public class ViewController extends BaseController {
}
