package com.cooperfilme.data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.cooperfilme.entity.Script;
import com.cooperfilme.entity.User;
import com.cooperfilme.service.ScriptService;
import com.cooperfilme.service.UserService;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private UserService userService;
    
    @Autowired
    private ScriptService scriptService;

    @Override
    public void run(String... args) throws Exception {
        // Credenciais para o README
        // Analista: analista@cooperfilme.com.br / password
        // Revisor: revisor@cooperfilme.com.br / password
        // Aprovador1: aprovador1@cooperfilme.com.br / password
        // Aprovador2: aprovador2@cooperfilme.com.br / password
        // Aprovador3: aprovador3@cooperfilme.com.br / password

        if (userService.findByEmail("analista@cooperfilme.com.br").isEmpty()) {
            User analista = new User();
            analista.setName("Analista Cooperfilme");
            analista.setEmail("analista@cooperfilme.com.br");
            analista.setPassword("password");
            analista.setRole(User.Role.ANALISTA);
            userService.save(analista);
        }

        if (userService.findByEmail("revisor@cooperfilme.com.br").isEmpty()) {
            User revisor = new User();
            revisor.setName("Revisor Cooperfilme");
            revisor.setEmail("revisor@cooperfilme.com.br");
            revisor.setPassword("password");
            revisor.setRole(User.Role.REVISOR);
            userService.save(revisor);
        }

        if (userService.findByEmail("aprovador1@cooperfilme.com.br").isEmpty()) {
            User aprovador1 = new User();
            aprovador1.setName("Aprovador 1 Cooperfilme");
            aprovador1.setEmail("aprovador1@cooperfilme.com.br");
            aprovador1.setPassword("password");
            aprovador1.setRole(User.Role.APROVADOR);
            userService.save(aprovador1);
        }

        if (userService.findByEmail("aprovador2@cooperfilme.com.br").isEmpty()) {
            User aprovador2 = new User();
            aprovador2.setName("Aprovador 2 Cooperfilme");
            aprovador2.setEmail("aprovador2@cooperfilme.com.br");
            aprovador2.setPassword("password");
            aprovador2.setRole(User.Role.APROVADOR);
            userService.save(aprovador2);
        }

        if (userService.findByEmail("aprovador3@cooperfilme.com.br").isEmpty()) {
            User aprovador3 = new User();
            aprovador3.setName("Aprovador 3 Cooperfilme");
            aprovador3.setEmail("aprovador3@cooperfilme.com.br");
            aprovador3.setPassword("password");
            aprovador3.setRole(User.Role.APROVADOR);
            userService.save(aprovador3);
        }
        
        //Scripts de carga pra teste
        if(scriptService.findAll().isEmpty()) {
        	// Script 1: O Último Trem para Aurora
            Script script1 = new Script();
            script1.setContent("**Título: O Último Trem para Aurora**\n\n**Cena 1: Estação Central – Noite**\nSom de apitos e passos apressados. Câmera foca em LUCAS (30), olhando o relógio com ansiedade.\n\n**LUCAS:** (voz trêmula) Se eu perder esse trem, perco tudo.\n\n**Cena 2: Vagão do Trem – Alguns minutos depois**\nLuzes piscam, passageiros quietos. Ele encontra CLARA (28), surpresa.\n\n**CLARA:** Você aqui? Depois de tudo?\n\n**LUCAS:** Não podia deixar você ir sem saber a verdade.\n\n**Cena 3: Flashback – Dia da Separação**\nBriga intensa na sala de estar. LUCAS quebra um vaso sem querer.\n\n**CLARA:** (chorando) Eu te amei, Lucas. Mas não posso mais viver com segredos.\n\n**Cena 4: Estação Aurora – Amanhecer**\nTrem para. Os dois em silêncio, mãos entrelaçadas.\n\n**LUCAS:** Será que podemos tentar de novo?\n\n**CLARA:** Vamos descer e descobrir.\n\n(Câmera se afasta, mostrando a cidade despertando com o sol.)\n\n**FIM.**");
            script1.setClientName("Marcio Willian");
            script1.setClientEmail("marcio@gmail.com");
            script1.setClientPhone("98999999999");
            scriptService.save(script1);

            // Script 2: Detetive aposentado
            Script script2 = new Script();
            script2.setContent("Um detetive aposentado é chamado para resolver o mistério do desaparecimento de um influente político em uma cidade litorânea.");
            script2.setClientName("Joana Mendes");
            script2.setClientEmail("joana.mendes@email.com");
            script2.setClientPhone("11987654321");
            scriptService.save(script2);

            // Script 3: Adolescente com segredo místico
            Script script3 = new Script();
            script3.setContent("Uma adolescente descobre que sua família guarda um segredo místico milenar que pode salvar ou destruir o mundo moderno.");
            script3.setClientName("Carlos Silva");
            script3.setClientEmail("carlos.silva@email.com");
            script3.setClientPhone("21998877665");
            scriptService.save(script3);

            // Script 4: Astronautas em apuros
            Script script4 = new Script();
            script4.setContent("Um grupo de astronautas fica preso em uma estação espacial após uma tempestade solar e precisa lutar contra o tempo para retornar à Terra.");
            script4.setClientName("Marina Rocha");
            script4.setClientEmail("marina.rocha@email.com");
            script4.setClientPhone("31997766554");
            scriptService.save(script4);

            // Script 5: Romance e segredos na pandemia
            Script script5 = new Script();
            script5.setContent("Durante a pandemia, dois estranhos se conhecem por vídeo chamada e desenvolvem um vínculo inesperado, mas escondem segredos perigosos.");
            script5.setClientName("Diego Albuquerque");
            script5.setClientEmail("diego.alb@email.com");
            script5.setClientPhone("61994433221");
            scriptService.save(script5);

            // Script 6: Chef e lavagem de dinheiro
            Script script6 = new Script();
            script6.setContent("Um chef falido decide abrir um food truck e acaba se envolvendo com uma gangue que usa o negócio como fachada para lavagem de dinheiro.");
            script6.setClientName("Fernanda Lopes");
            script6.setClientEmail("fernanda.lopes@email.com");
            script6.setClientPhone("47991122334");
            scriptService.save(script6);
        }        
    }
}

