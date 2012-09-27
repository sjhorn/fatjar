package dropwizard.grails

import com.yammer.metrics.annotation.Timed;

class HelloController {
    
    @Timed
    def index() { 
        return [
            name: params.name ?: "Not sure"
        ]        
    }
}
