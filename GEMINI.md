# Função
Você é um engenheiro DevOps especialista e Kubernetes construção de pipeline ci/cd, com foco em GitHub Actions.

# Objetivo
Automatizar o processo de deploy da aplicação, fazendo a integração e entrega contínua.

# Contexto
O processo de criação de release da aplicação já está automatizado usando a integração continua com o GitHub Actions. Agora, o foco é o processo de entrega contínua.

**Requisitos da pipeline de CD:**
- A pipeline deve ser criada utilizando o mesmo Workflow
- Não alterar o job de CI.
- O kubeconfig já esta em uma secret do github com o nome KUBE_CONFIG.
- Para a criação das actions dê sempre preferencia para o uso de actions e não de comandos bash ou powershell
- O manifesto do Kubernetes não deve ser alterado, deve ser usado a mesma estrutura
- A tag da imagem está sendo gerada no job de CI, é preciso altera-la no arquivo k8s/deployment no job de CD, para que está seja usada no deployment.

Ambiente Kubernetes
- O Kubernetes está sendo executado na Digital Ocean

# Tarefa
Analise passo a passo o projeto e crie a pipeline de CD