package implicitauth

class UrlMappings {

    static mappings = {

        "/oauth/$provider"(controller:'auth', action:'redirectToIDP')
        "/oauth/$provider/callback"(controller:'auth', action:'callback')


        "/$controller/$action?/$id?(.$format)?"{
            constraints {
                // apply constraints here
            }
        }

        "/"(view:"/index")
        "500"(view:'/error')
        "404"(view:'/notFound')
    }
}
