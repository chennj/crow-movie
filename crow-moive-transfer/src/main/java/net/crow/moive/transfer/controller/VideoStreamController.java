package net.crow.moive.transfer.controller;

import org.springframework.http.ResponseEntity;
//import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.crow.moive.transfer.service.VideoStreamService;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/video")
public class VideoStreamController {

	private final VideoStreamService videoStreamService;

    public VideoStreamController(VideoStreamService videoStreamService) {
        this.videoStreamService = videoStreamService;
    }
    
    //eg: http://localhost:8080/video/stream/mp4/toystory
    @GetMapping("/stream/{fileType}/{fileName}")
    public Mono<ResponseEntity<byte[]>> streamVideo(
    		//ServerHttpResponse serverHttpResponse, 
    		@RequestHeader(value = "Range", required = false) String httpRangeList,
    		@PathVariable("fileType") String fileType,
    		@PathVariable("fileName") String fileName) {
        return Mono.just(videoStreamService.prepareContent(fileName, fileType, httpRangeList));
    }
}
