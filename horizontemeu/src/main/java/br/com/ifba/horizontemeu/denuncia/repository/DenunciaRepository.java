package br.com.ifba.horizontemeu.denuncia.repository;

import br.com.ifba.horizontemeu.comentario.entity.Comentario;
import br.com.ifba.horizontemeu.denuncia.entity.Denuncia;
import br.com.ifba.horizontemeu.denuncia.enums.StatusDenuncia;
import br.com.ifba.horizontemeu.foto.entity.Foto;
import br.com.ifba.horizontemeu.usuario.entity.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DenunciaRepository extends JpaRepository<Denuncia, Long> {

    //Denúncias feitas por um usuario
    List<Denuncia> findByUsuario(Usuario usuario);

    //Fila do admin filtrada por status
    Page<Denuncia> findByStatus(StatusDenuncia status, Pageable pageable);

    List<Denuncia> findByFoto(Foto foto);
    List<Denuncia> findByComentario(Comentario comentario);
}
