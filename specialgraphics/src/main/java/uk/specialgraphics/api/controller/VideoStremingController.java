package uk.specialgraphics.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.specialgraphics.api.payload.request.StreamVideoUrlRequest;
import uk.specialgraphics.api.service.StremingService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping(value = "/videoStreming")
public class VideoStremingController {

    @Autowired
    private StremingService stremingService;

    @GetMapping(value = "/video")
    public ResponseEntity<Resource> streamVideo(
            StreamVideoUrlRequest streamVideoUrlRequest,
            HttpServletRequest request,
            HttpServletResponse response) throws IOException {

        return stremingService.getResource(streamVideoUrlRequest, request, response);
    }
}
