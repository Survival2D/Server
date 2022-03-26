FROM node:alpine AS builder
#RUN apk --no-cache add git

WORKDIR /builder

#COPY node_modules .
#COPY package.json .
#COPY package-lock.json .
#COPY src .
#COPY tsconfig.json .
#RUN npm install

COPY . .
RUN npx tsc

FROM heroiclabs/nakama:3.6.0

COPY --from=builder /builder /nakama/data
#COPY local.yml /nakama/data
