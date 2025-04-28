package br.com.rodrigo.gestortarefas.api.rest;

import br.com.rodrigo.gestortarefas.api.model.form.EmpresaForm;
import br.com.rodrigo.gestortarefas.api.model.response.EmpresaResponse;
import br.com.rodrigo.gestortarefas.api.services.HorarioFuncionamentoService;
import br.com.rodrigo.gestortarefas.api.services.IEmpresa;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Optional;

@RestController
@RequestMapping("/empresas")
@RequiredArgsConstructor
public class EmpresaController extends ControllerBase<EmpresaResponse> {

    private final IEmpresa empresaService;
    private final HorarioFuncionamentoService horarioFuncionamentoServico;

    @PostMapping
    public ResponseEntity<EmpresaResponse> criar(@RequestBody @Valid EmpresaForm empresaForm) {
        EmpresaResponse response = empresaService.criar(empresaForm, null);
        return responderItemCriadoComURI(response, ServletUriComponentsBuilder.fromCurrentRequest(), "/{id}", response.getId().toString());
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmpresaResponse> atualizar(@PathVariable @Valid Long id, @RequestBody EmpresaForm empresaForm) {
        EmpresaResponse response = empresaService.criar(empresaForm, id);
        return responderSucessoComItem(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<EmpresaResponse> deletar(@PathVariable Long id) {
        empresaService.deletar(id);
        return responderSucesso();
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmpresaResponse> consultarPorId(@PathVariable Long id) {
        Optional<EmpresaResponse> response = empresaService.consultarPorId(id);
        return response.map(this::responderSucessoComItem)
                .orElseGet(this::responderItemNaoEncontrado);
    }

    @GetMapping()
    public ResponseEntity<Page<EmpresaResponse>> listarTodos(@RequestParam int page,
                                                            @RequestParam int size,
                                                            @RequestParam(required = false) String sort,
                                                            @RequestParam(required = false) String nome,
                                                            @RequestParam(required = false) String cnpj,
                                                             @RequestParam(required = false) String telefone) {
        Page<EmpresaResponse> empresa = empresaService.listarTodos(page, size, sort, nome, cnpj, telefone);
        return responderListaDeItensPaginada(empresa);
    }

    @GetMapping("/esta-aberto")
    public ResponseEntity<Boolean> estaAberto() {
        boolean aberto = horarioFuncionamentoServico.estaAbertoAgora();
        return ResponseEntity.ok(aberto);
    }
}