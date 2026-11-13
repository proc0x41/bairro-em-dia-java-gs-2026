package br.com.bairroemdia.config;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.config.annotation.*;
@Configuration
public class ApiConfig implements WebMvcConfigurer {
    @Value("${app.cors.origins}") private String[] origins;
    @Bean public ModelMapper modelMapper() { ModelMapper m=new ModelMapper(); m.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT); return m; }
    @Override public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedOrigins(origins).allowedMethods("GET","POST","PUT","DELETE","OPTIONS").allowedHeaders("Content-Type").exposedHeaders("Location");
    }
}
