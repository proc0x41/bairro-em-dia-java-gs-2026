package br.com.bairroemdia.service;
import br.com.bairroemdia.dao.*;
import br.com.bairroemdia.dto.*;
import br.com.bairroemdia.model.*;
import br.com.bairroemdia.exception.*;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.*;
@Service
@Transactional
public class ZeladoriaService {
    private final CategoriaDao categorias;
    private final OcorrenciaDao ocorrencias;
    private final AtualizacaoDao atualizacoes;
    private final ModelMapper mapper;
    public ZeladoriaService(CategoriaDao c, OcorrenciaDao o, AtualizacaoDao a, ModelMapper m) { categorias=c; ocorrencias=o; atualizacoes=a; mapper=m; }
    public List<CategoriaSaida> listarCategorias() { return categorias.listar().stream().map(m->mapper.map(m,CategoriaSaida.class)).toList(); }
    public CategoriaSaida categoria(Long id) { return mapper.map(categorias.buscar(id).orElseThrow(()->new NaoEncontradoException("Categoria não encontrada.")),CategoriaSaida.class); }
    public CategoriaSaida salvarCategoria(Long id, CategoriaEntrada dto) {
        if(id!=null) categoria(id);
        Categoria m=mapper.map(dto,Categoria.class); m.setId(id);
        return mapper.map(id==null?categorias.inserir(m):categorias.atualizar(m),CategoriaSaida.class);
    }
    public void removerCategoria(Long id) { categoria(id); categorias.remover(id); }
    public List<OcorrenciaSaida> listarOcorrencias() { return ocorrencias.listar().stream().map(m->mapper.map(m,OcorrenciaSaida.class)).toList(); }
    public OcorrenciaSaida ocorrencia(Long id) { return mapper.map(modelOcorrencia(id),OcorrenciaSaida.class); }
    private Ocorrencia modelOcorrencia(Long id) { return ocorrencias.buscar(id).orElseThrow(()->new NaoEncontradoException("Ocorrência não encontrada.")); }
    public OcorrenciaSaida salvarOcorrencia(Long id, OcorrenciaEntrada dto) {
        categoria(dto.getCategoriaId());
        Ocorrencia anterior=id==null?null:modelOcorrencia(id);
        if(anterior==null && !"ABERTA".equals(dto.getStatus())) throw new RegraNegocioException("Toda ocorrência deve ser cadastrada com status ABERTA.");
        if(anterior!=null && !Objects.equals(anterior.getStatus(),dto.getStatus())) {
            Map<String,List<String>> fluxo=Map.of("ABERTA",List.of("EM_ANALISE"),"EM_ANALISE",List.of("EM_ANDAMENTO"),"EM_ANDAMENTO",List.of("RESOLVIDA"),"RESOLVIDA",List.of());
            if(!fluxo.get(anterior.getStatus()).contains(dto.getStatus())) throw new RegraNegocioException("Transição inválida. Siga a ordem: aberta, em análise, em andamento e resolvida.");
        }
        if(!"RESOLVIDA".equals(dto.getStatus())) {
            boolean duplicada=ocorrencias.listar().stream().anyMatch(o->!Objects.equals(o.getId(),id) && !"RESOLVIDA".equals(o.getStatus()) && Objects.equals(o.getCategoriaId(),dto.getCategoriaId()) && o.getEndereco().strip().equalsIgnoreCase(dto.getEndereco().strip()) && o.getBairro().strip().equalsIgnoreCase(dto.getBairro().strip()));
            if(duplicada) throw new RegraNegocioException("Já existe uma ocorrência ativa dessa categoria no mesmo endereço e bairro. Acompanhe o protocolo existente.");
        }
        Ocorrencia m=mapper.map(dto,Ocorrencia.class); m.setId(id); m.setCriadoEm(anterior==null?LocalDateTime.now():anterior.getCriadoEm());
        Ocorrencia salvo=id==null?ocorrencias.inserir(m):ocorrencias.atualizar(m);
        if(anterior!=null && !Objects.equals(anterior.getStatus(),m.getStatus())) {
            Atualizacao a=new Atualizacao(); a.setOcorrenciaId(id); a.setAutor("Sistema"); a.setMensagem("Status alterado para "+m.getStatus()+"."); a.setCriadoEm(LocalDateTime.now()); atualizacoes.inserir(a);
        }
        return mapper.map(salvo,OcorrenciaSaida.class);
    }
    public void removerOcorrencia(Long id) { ocorrencia(id); ocorrencias.remover(id); }
    public List<AtualizacaoSaida> listarAtualizacoes() { return atualizacoes.listar().stream().map(m->mapper.map(m,AtualizacaoSaida.class)).toList(); }
    public List<AtualizacaoSaida> porOcorrencia(Long id) { ocorrencia(id); return atualizacoes.porOcorrencia(id).stream().map(m->mapper.map(m,AtualizacaoSaida.class)).toList(); }
    public AtualizacaoSaida atualizacao(Long id) { return mapper.map(atualizacoes.buscar(id).orElseThrow(()->new NaoEncontradoException("Atualização não encontrada.")),AtualizacaoSaida.class); }
    public AtualizacaoSaida salvarAtualizacao(Long id, AtualizacaoEntrada dto) {
        ocorrencia(dto.getOcorrenciaId());
        Atualizacao anterior=id==null?null:atualizacoes.buscar(id).orElseThrow(()->new NaoEncontradoException("Atualização não encontrada."));
        if(anterior!=null && !Objects.equals(anterior.getOcorrenciaId(),dto.getOcorrenciaId())) throw new RegraNegocioException("Uma atualização não pode ser transferida para outra ocorrência.");
        Atualizacao m=mapper.map(dto,Atualizacao.class); m.setId(id); m.setCriadoEm(anterior==null?LocalDateTime.now():anterior.getCriadoEm());
        return mapper.map(id==null?atualizacoes.inserir(m):atualizacoes.atualizar(m),AtualizacaoSaida.class);
    }
    public void removerAtualizacao(Long id) { atualizacao(id); atualizacoes.remover(id); }
}
