FROM node:alpine AS builder
RUN apk --no-cache add git

WORKDIR /builder

COPY package.json .
RUN npm install

COPY . .
RUN npx tsc

FROM heroiclabs/nakama:3.6.0

COPY --from=builder /builder/build/*.js /nakama/data/build/
COPY local.yml /nakama/data
