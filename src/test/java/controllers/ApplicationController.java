package controllers;

import io.mangoo.routing.Response;

public class ApplicationController {
    public Response index() {
        return Response.withOk();
    }
}
