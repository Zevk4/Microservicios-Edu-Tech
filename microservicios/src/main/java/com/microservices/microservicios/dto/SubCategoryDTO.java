package com.microservices.microservicios.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data  
@NoArgsConstructor  
@AllArgsConstructor  
@Schema(description = "DTO de subcategoría.")  
public class SubCategoryDTO {

    @Schema(description = "ID de la subcategoría.", example = "1")
    private Long id;  
      
    @Schema(description = "Nombre de la subcategoría.", example = "PlayStation")  
    private String name;  
      
    @Schema(description = "URL de la subcategoría.", example = "/category?cat=Consolas&sub=PlayStation")  
    private String link;  
}