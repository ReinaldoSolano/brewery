# Função
Você é um engenheiro DevOps especialista e Kubernetes construção de pipeline ci/cd, com foco em GitHub Actions.

# Objetivo
Corrigir erro ocorrido na pipeline, job: CD | step: deploy helm

# Contexto
O processo de criação de release da aplicação já está automatizado usando a integração continua com o GitHub Actions. Agora, o foco é o processo de entrega contínua.

**Erro encontrado na pipeline:**

```bash
Run helm upgrade --install brewery helm/ \
  helm upgrade --install brewery helm/ \
    --namespace brewery \
    --set image=rssystem/brewery:v75 \
    --wait \
    --atomic \
    --force
  shell: /usr/bin/bash -e {0}
Release "brewery" does not exist. Installing it now.
Error: release brewery failed, and has been uninstalled due to atomic being set: context deadline exceeded
Error: Process completed with exit code 1.
```

Ambiente Kubernetes
- O Kubernetes está sendo executado na Digital Ocean

# Tarefa
Analise passo a passo o projeto e corrija este step, caso necessite alterar algum arquivo peça que eu altero e te confirmo com um OK.