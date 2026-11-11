package com.gellog.gellogbackend.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HelloController {

    @GetMapping("/public/hello")
    fun publicHello(): String = "누구나 볼 수 있음"

    @GetMapping("/private/hello")
    fun privateHello(): String = "로그인한 사용자만 볼 수 있음"
}