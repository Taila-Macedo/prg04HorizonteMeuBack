package br.com.ifba.horizontemeu.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public String uploadFoto(MultipartFile arquivo) {
        try {
            Map resultado = cloudinary.uploader().upload(
                    arquivo.getBytes(),
                    ObjectUtils.asMap(
                            "folder", "horizonte-meu/fotos",
                            "resource_type", "image"
                    )
            );
            return (String) resultado.get("secure_url");
        } catch (IOException e) {
            throw new RuntimeException("Erro ao fazer upload da foto no Cloudinary", e);
        }
    }
}