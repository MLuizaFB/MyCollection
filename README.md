# MyCollection 📷

Um aplicativo Android nativo desenvolvido para catalogar e gerenciar itens de coleções físicas (como câmeras digitais antigas). O projeto foi construído com foco no aprendizado de arquitetura limpa, segurança de dados (AppSec) e performance.

O aplicativo foi desenvolvido exclusivamente para praticar e consolidar meus conhecimentos em desenvolvimento Android nativo, não possuindo propósitos comerciais ou fins lucrativos.

🚧 **Status do Projeto:** O app ainda está em desenvolvimento ativo, visando melhorias contínuas e a adição de novas funcionalidades.

## 📱 Principais Telas e Funcionalidades
* **Tela Inicial:** Visualização organizada das categorias cadastradas e dos itens pertencentes à categoria selecionada.
* **Visualização Detalhada:** Tela individual para conferir de perto as informações e imagens de um item específico.
* **Tela de Perfil:** Espaço dedicado às informações do usuário e estatísticas de uso.
* **Gerenciamento Completo (CRUD):** Criação, consulta, edição e exclusão de categorias e itens (edição de itens em desenvolvimento). 
* **Edição de Perfil:** Permite a customização e atualização dos dados do usuário.

## 🖼️ Screenshots (Demonstração)

## 🚀 Tecnologias e Arquitetura
* **Kotlin** nativo
* **Room Database:** Armazenamento local com integridade relacional (Cascade Delete).
* **Glide:** Gerenciamento eficiente de cache e carregamento de mídias na interface.
* **Coroutines:** Operações assíncronas tratadas adequadamente (`Dispatchers.IO`) para garantir fluidez e evitar travamentos na Main Thread.
* **Google Sign-In:** Autenticação via Google Play Services sem exposição de credenciais no código-fonte.
  * *Nota de Aprendizado:* A integração com esta API foi feita com o propósito de estudar a comunicação com serviços do ecossistema Google. Atualmente, o recurso cumpre o papel de coletar o nome e a foto do usuário para personalização da interface. Tornar o uso dessa autenticação ainda mais significativo no ecossistema do app é um dos objetivos mapeados para o futuro.

## 🛡️ Destaques de Segurança (AppSec)
O aplicativo foi estruturado seguindo boas práticas de segurança recomendadas para o desenvolvimento mobile:
* **Sanitização de Armazenamento:** Implementação de rotinas de limpeza para apagar arquivos físicos órfãos no armazenamento interno (`filesDir`) sempre que um item ou categoria é excluído/editado, evitando vazamento de dados residuais e acúmulo desnecessário de cache no dispositivo.
* **Segurança de Credenciais:** Arquivos sensíveis de configuração e ambiente (`google-services.json` e `local.properties`) estritamente bloqueados via `.gitignore` para evitar vazamento em repositórios públicos.
* **Sandboxing de Arquivos:** Utilização do armazenamento interno privado do aplicativo, garantindo que as mídias da coleção fiquem isoladas e protegidas contra acesso não autorizado por parte de outros aplicativos instalados no dispositivo.

## 🛠️ Como rodar este projeto localmente
Para garantir a segurança das credenciais, este repositório não inclui o arquivo de configuração do Google. Para clonar e rodar o projeto em sua máquina, siga os passos:

1. Crie um projeto no [Firebase Console](https://console.firebase.google.com/).
2. Adicione um aplicativo Android utilizando o pacote `com.example.mycollection`.
3. Adicione o SHA-1 da sua chave de desenvolvimento (Debug) nas configurações do aplicativo no console do Firebase.
4. Baixe o arquivo gerado `google-services.json` e coloque-o dentro do diretório `app/` do projeto clonado.
5. Sincronize as dependências do Gradle no Android Studio e execute o app no emulador ou dispositivo físico.
