package com.amz.scm.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Contact Manager API v2")
                        .version("2.0")
                         .description("""
### 🚀 API Version: **v2**

Introducing the enhanced and optimized image handling API powered by **Amazon AWS S3** technology.

---

### ✨ Key Features

- 🔐 **Spring Security** integration for robust access control  
- 🛡️ **JWT-based API Access** for secure and stateless authentication  
- ☁️ **Amazon AWS S3** used for reliable and scalable image storage  
- 📦 **Optimized Uploads** with pre-signed URL support  
- 🔁 **Better Response Handling** with standardized success & error formats  
- 🧪 **Swagger UI v3 Integration** for seamless API testing and documentation  
- 📊 **Structured Logging** for easier debugging and monitoring  
- ⏱️ **Improved Performance** and reduced response latency  
- 📂 **Versioned API Support** for backward compatibility  

---

Upgrade to **v2** and experience a cleaner, faster, and more secure way to manage media with your backend.
""")
                );

    }

}

// .description("API Version v2, provides Enhanced and Optimized Image Handling
// with Amazon Aws S3 Technology.<br> <h1><b>Features</b></h1><br>1.Spring
// Security <br> 2.Jwt Based Api Access <br> 3.Aws S3, for storing objects <br>
// 4.Enhanced Response and Error Handling\n"));
