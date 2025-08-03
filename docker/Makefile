SHELL := /bin/bash

# Spring profile, docker compose file
ENV ?= dev
VALID_ENVS := dev prod dev_flyway

CONTAINER_ENV_FILE := .env.container.$(ENV)
COMPOSE_CMD = docker-compose -f docker-compose.$(ENV).yml --env-file $(CONTAINER_ENV_FILE)

ECHO_TXT := "docker-compose with spring profile $(ENV)"


.PHONY: up build down shell check-env

up:
	@echo "Starting " $(ECHO_TXT)
	$(COMPOSE_CMD) up -d

build:
	@echo "Building " $(ECHO_TXT)
	$(COMPOSE_CMD) build

down:
	@echo "Stopping " $(ECHO_TXT)
	$(COMPOSE_CMD) down

shell:
ifeq ($(strip $(APP_SERVICE)),)
	$(error Provide ENV in target, eg ENV=dev)
endif
	$(ECHO_TXT)
	docker-compose -f docker-compose.$(ENV).yml --env-file $(CONTAINER_ENV_FILE) exec $(APP_SERVICE) sh
# dziala ale 'sh' nie ma na jre, mega okrojona jest

