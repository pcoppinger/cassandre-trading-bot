(window["webpackJsonp"] = window["webpackJsonp"] || []).push([[12],{

/***/ "../../../../.config/yarn/global/node_modules/cache-loader/dist/cjs.js?{\"cacheDirectory\":\"../../../../.config/yarn/global/node_modules/@vuepress/core/node_modules/.cache/vuepress\",\"cacheIdentifier\":\"4b61893c-vue-loader-template\"}!../../../../.config/yarn/global/node_modules/vue-loader/lib/loaders/templateLoader.js?!../../../../.config/yarn/global/node_modules/cache-loader/dist/cjs.js?!../../../../.config/yarn/global/node_modules/vue-loader/lib/index.js?!../../../../.config/yarn/global/node_modules/@vuepress/markdown-loader/index.js?!./src/learn/deploy-and-run/docker.md?vue&type=template&id=5629d19a&":
/*!**********************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************!*\
  !*** /home/runner/.config/yarn/global/node_modules/cache-loader/dist/cjs.js?{"cacheDirectory":"../../../../.config/yarn/global/node_modules/@vuepress/core/node_modules/.cache/vuepress","cacheIdentifier":"4b61893c-vue-loader-template"}!/home/runner/.config/yarn/global/node_modules/vue-loader/lib/loaders/templateLoader.js??vue-loader-options!/home/runner/.config/yarn/global/node_modules/cache-loader/dist/cjs.js??ref--1-0!/home/runner/.config/yarn/global/node_modules/vue-loader/lib??ref--1-1!/home/runner/.config/yarn/global/node_modules/@vuepress/markdown-loader??ref--1-2!./src/learn/deploy-and-run/docker.md?vue&type=template&id=5629d19a& ***!
  \**********************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************/
/*! exports provided: render, staticRenderFns */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "render", function() { return render; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "staticRenderFns", function() { return staticRenderFns; });
var render = function () {var _vm=this;var _h=_vm.$createElement;var _c=_vm._self._c||_h;return _c('ContentSlotsDistributor',{attrs:{"slot-key":_vm.$parent.slotKey}},[_c('h1',{attrs:{"id":"deploy-run"}},[_c('a',{staticClass:"header-anchor",attrs:{"href":"#deploy-run"}},[_vm._v("#")]),_vm._v(" Deploy & run")]),_vm._v(" "),_c('h2',{attrs:{"id":"server-installation"}},[_c('a',{staticClass:"header-anchor",attrs:{"href":"#server-installation"}},[_vm._v("#")]),_vm._v(" Server installation")]),_vm._v(" "),_c('p',[_vm._v("We are starting with a fresh "),_c('a',{attrs:{"href":"https://releases.ubuntu.com/20.04/","target":"_blank","rel":"noopener noreferrer"}},[_vm._v("Ubuntu 20.04.2 LTS (Focal Fossa)"),_c('OutboundLink')],1),_vm._v(" installation on a dedicated server, and we will use "),_c('a',{attrs:{"href":"https://www.docker.com/","target":"_blank","rel":"noopener noreferrer"}},[_vm._v("Docker"),_c('OutboundLink')],1),_vm._v(" to run our trading bot and the other components.")]),_vm._v(" "),_c('p',[_vm._v("This is how it works:")]),_vm._v(" "),_c('ul',[_c('li',[_vm._v("Two images are started manually on the server:\n"),_c('ul',[_c('li',[_vm._v("A "),_c('a',{attrs:{"href":"https://hub.docker.com/_/postgres","target":"_blank","rel":"noopener noreferrer"}},[_vm._v("Postgresql image"),_c('OutboundLink')],1),_vm._v(" to store strategies, orders, trades & positions.")]),_vm._v(" "),_c('li',[_vm._v("A "),_c('a',{attrs:{"href":"https://hub.docker.com/r/prodrigestivill/postgres-backup-local","target":"_blank","rel":"noopener noreferrer"}},[_vm._v("Postgresql backup image"),_c('OutboundLink')],1),_vm._v(" to backup Postgresql databases.")])])]),_vm._v(" "),_c('li',[_vm._v("Our trading bot is built as a Docker image and deployed to the server by our continuous integration server.")])]),_vm._v(" "),_c('div',{staticClass:"custom-block tip"},[_c('p',{staticClass:"custom-block-title"},[_vm._v("TIP")]),_vm._v(" "),_c('p',[_vm._v("We chose PostgreSQL as our database but you can choose the one you want, just add the corresponding JDBC driver to your "),_c('code',[_vm._v("pom.xml")]),_vm._v(".")])]),_vm._v(" "),_c('h3',{attrs:{"id":"install-useful-required-tools"}},[_c('a',{staticClass:"header-anchor",attrs:{"href":"#install-useful-required-tools"}},[_vm._v("#")]),_vm._v(" Install useful & required tools")]),_vm._v(" "),_c('div',{staticClass:"language-bash extra-class"},[_c('pre',{pre:true,attrs:{"class":"language-bash"}},[_c('code',[_c('span',{pre:true,attrs:{"class":"token function"}},[_vm._v("sudo")]),_vm._v(" "),_c('span',{pre:true,attrs:{"class":"token function"}},[_vm._v("apt")]),_vm._v(" update\n"),_c('span',{pre:true,attrs:{"class":"token function"}},[_vm._v("sudo")]),_vm._v(" "),_c('span',{pre:true,attrs:{"class":"token function"}},[_vm._v("apt")]),_vm._v(" -y "),_c('span',{pre:true,attrs:{"class":"token function"}},[_vm._v("install")]),_vm._v(" apt-transport-https ca-certificates "),_c('span',{pre:true,attrs:{"class":"token function"}},[_vm._v("curl")]),_vm._v(" gnupg2 pass software-properties-common\n"),_c('span',{pre:true,attrs:{"class":"token function"}},[_vm._v("sudo")]),_vm._v(" "),_c('span',{pre:true,attrs:{"class":"token function"}},[_vm._v("apt")]),_vm._v(" -y upgrade\n")])])]),_c('h3',{attrs:{"id":"install-docker-docker-compose"}},[_c('a',{staticClass:"header-anchor",attrs:{"href":"#install-docker-docker-compose"}},[_vm._v("#")]),_vm._v(" Install Docker & docker-compose")]),_vm._v(" "),_c('div',{staticClass:"language-bash extra-class"},[_c('pre',{pre:true,attrs:{"class":"language-bash"}},[_c('code',[_c('span',{pre:true,attrs:{"class":"token function"}},[_vm._v("sudo")]),_vm._v(" "),_c('span',{pre:true,attrs:{"class":"token function"}},[_vm._v("curl")]),_vm._v(" -fsSL https://download.docker.com/linux/ubuntu/gpg "),_c('span',{pre:true,attrs:{"class":"token operator"}},[_vm._v("|")]),_vm._v(" "),_c('span',{pre:true,attrs:{"class":"token function"}},[_vm._v("sudo")]),_vm._v(" apt-key "),_c('span',{pre:true,attrs:{"class":"token function"}},[_vm._v("add")]),_vm._v(" -\n"),_c('span',{pre:true,attrs:{"class":"token function"}},[_vm._v("sudo")]),_vm._v(" add-apt-repository "),_c('span',{pre:true,attrs:{"class":"token string"}},[_vm._v("\"deb [arch=amd64] https://download.docker.com/linux/ubuntu "),_c('span',{pre:true,attrs:{"class":"token variable"}},[_c('span',{pre:true,attrs:{"class":"token variable"}},[_vm._v("$(")]),_vm._v("lsb_release -cs"),_c('span',{pre:true,attrs:{"class":"token variable"}},[_vm._v(")")])]),_vm._v(" stable\"")]),_vm._v("\n"),_c('span',{pre:true,attrs:{"class":"token function"}},[_vm._v("sudo")]),_vm._v(" "),_c('span',{pre:true,attrs:{"class":"token function"}},[_vm._v("apt")]),_vm._v(" update\n"),_c('span',{pre:true,attrs:{"class":"token function"}},[_vm._v("sudo")]),_vm._v(" "),_c('span',{pre:true,attrs:{"class":"token function"}},[_vm._v("apt-cache")]),_vm._v(" policy docker-ce\n"),_c('span',{pre:true,attrs:{"class":"token function"}},[_vm._v("sudo")]),_vm._v(" "),_c('span',{pre:true,attrs:{"class":"token function"}},[_vm._v("apt")]),_vm._v(" -y "),_c('span',{pre:true,attrs:{"class":"token function"}},[_vm._v("install")]),_vm._v(" docker-ce docker-compose\n"),_c('span',{pre:true,attrs:{"class":"token function"}},[_vm._v("sudo")]),_vm._v(" "),_c('span',{pre:true,attrs:{"class":"token function"}},[_vm._v("chmod")]),_vm._v(" "),_c('span',{pre:true,attrs:{"class":"token number"}},[_vm._v("666")]),_vm._v(" /var/run/docker.sock\n")])])]),_c('h3',{attrs:{"id":"add-a-user-for-the-trading-bot"}},[_c('a',{staticClass:"header-anchor",attrs:{"href":"#add-a-user-for-the-trading-bot"}},[_vm._v("#")]),_vm._v(" Add a user for the trading bot")]),_vm._v(" "),_c('p',[_vm._v("Our bot will be deployed to this server from another server (in our case, our continuous integration server), so we need to create a user that can connect with SSH:")]),_vm._v(" "),_c('div',{staticClass:"language-bash extra-class"},[_c('pre',{pre:true,attrs:{"class":"language-bash"}},[_c('code',[_c('span',{pre:true,attrs:{"class":"token function"}},[_vm._v("sudo")]),_vm._v(" "),_c('span',{pre:true,attrs:{"class":"token function"}},[_vm._v("useradd")]),_vm._v(" -m -d /home/sma-trading-bot sma-trading-bot\n"),_c('span',{pre:true,attrs:{"class":"token function"}},[_vm._v("sudo")]),_vm._v(" "),_c('span',{pre:true,attrs:{"class":"token function"}},[_vm._v("passwd")]),_vm._v(" sma-trading-bot\n"),_c('span',{pre:true,attrs:{"class":"token function"}},[_vm._v("sudo")]),_vm._v(" gpasswd -a sma-trading-bot docker\n"),_c('span',{pre:true,attrs:{"class":"token function"}},[_vm._v("sudo")]),_vm._v(" "),_c('span',{pre:true,attrs:{"class":"token function"}},[_vm._v("usermod")]),_vm._v(" --shell /bin/bash sma-trading-bot\n")])])]),_c('h2',{attrs:{"id":"docker-images-on-the-server"}},[_c('a',{staticClass:"header-anchor",attrs:{"href":"#docker-images-on-the-server"}},[_vm._v("#")]),_vm._v(" Docker images on the server")]),_vm._v(" "),_c('p',[_vm._v("Download the "),_c('a',{attrs:{"href":"https://github.com/cassandre-tech/cassandre-trading-bot/blob/development/trading-bot-server/docker-compose.yml","target":"_blank","rel":"noopener noreferrer"}},[_vm._v("docker-compose.yml"),_c('OutboundLink')],1),_vm._v(" file on your server, edit your preferences (password, timezone, backup settings...) and run it with the command:")]),_vm._v(" "),_c('div',{staticClass:"language-bash extra-class"},[_c('pre',{pre:true,attrs:{"class":"language-bash"}},[_c('code',[_c('span',{pre:true,attrs:{"class":"token function"}},[_vm._v("sudo")]),_vm._v(" docker-compose up -d\n")])])]),_c('div',{staticClass:"custom-block tip"},[_c('p',{staticClass:"custom-block-title"},[_vm._v("TIP")]),_vm._v(" "),_c('p',[_vm._v("You can download it directly with the command : "),_c('code',[_vm._v("curl -o docker-compose.yml https://raw.githubusercontent.com/cassandre-tech/cassandre-trading-bot/development/trading-bot-server/docker-compose.yml")])])]),_vm._v(" "),_c('h3',{attrs:{"id":"network"}},[_c('a',{staticClass:"header-anchor",attrs:{"href":"#network"}},[_vm._v("#")]),_vm._v(" Network")]),_vm._v(" "),_c('div',{staticClass:"language-yaml extra-class"},[_c('pre',{pre:true,attrs:{"class":"language-yaml"}},[_c('code',[_c('span',{pre:true,attrs:{"class":"token key atrule"}},[_vm._v("networks")]),_c('span',{pre:true,attrs:{"class":"token punctuation"}},[_vm._v(":")]),_vm._v("\n  "),_c('span',{pre:true,attrs:{"class":"token key atrule"}},[_vm._v("cassandre")]),_c('span',{pre:true,attrs:{"class":"token punctuation"}},[_vm._v(":")]),_vm._v("\n    "),_c('span',{pre:true,attrs:{"class":"token key atrule"}},[_vm._v("name")]),_c('span',{pre:true,attrs:{"class":"token punctuation"}},[_vm._v(":")]),_vm._v(" cassandre\n")])])]),_c('p',[_vm._v("This part declares a network named "),_c('code',[_vm._v("cassandre")]),_vm._v(".")]),_vm._v(" "),_c('h3',{attrs:{"id":"volumes"}},[_c('a',{staticClass:"header-anchor",attrs:{"href":"#volumes"}},[_vm._v("#")]),_vm._v(" Volumes")]),_vm._v(" "),_c('div',{staticClass:"language-yaml extra-class"},[_c('pre',{pre:true,attrs:{"class":"language-yaml"}},[_c('code',[_c('span',{pre:true,attrs:{"class":"token key atrule"}},[_vm._v("volumes")]),_c('span',{pre:true,attrs:{"class":"token punctuation"}},[_vm._v(":")]),_vm._v("\n  "),_c('span',{pre:true,attrs:{"class":"token key atrule"}},[_vm._v("cassandre_database")]),_c('span',{pre:true,attrs:{"class":"token punctuation"}},[_vm._v(":")]),_vm._v("\n  "),_c('span',{pre:true,attrs:{"class":"token key atrule"}},[_vm._v("cassandre_database_backup")]),_c('span',{pre:true,attrs:{"class":"token punctuation"}},[_vm._v(":")]),_vm._v("\n")])])]),_c('p',[_vm._v("This part declares two volumes (space on disk) :")]),_vm._v(" "),_c('ul',[_c('li',[_c('code',[_vm._v("cassandre_database")]),_vm._v(" for the database.")]),_vm._v(" "),_c('li',[_c('code',[_vm._v("cassandre_database_backup")]),_vm._v(" for the database backups.")])]),_vm._v(" "),_c('h3',{attrs:{"id":"postgresql"}},[_c('a',{staticClass:"header-anchor",attrs:{"href":"#postgresql"}},[_vm._v("#")]),_vm._v(" Postgresql")]),_vm._v(" "),_c('div',{staticClass:"language-yaml extra-class"},[_c('pre',{pre:true,attrs:{"class":"language-yaml"}},[_c('code',[_vm._v("  "),_c('span',{pre:true,attrs:{"class":"token key atrule"}},[_vm._v("cassandre-postgresql")]),_c('span',{pre:true,attrs:{"class":"token punctuation"}},[_vm._v(":")]),_vm._v("\n    "),_c('span',{pre:true,attrs:{"class":"token key atrule"}},[_vm._v("image")]),_c('span',{pre:true,attrs:{"class":"token punctuation"}},[_vm._v(":")]),_vm._v(" library/postgres"),_c('span',{pre:true,attrs:{"class":"token punctuation"}},[_vm._v(":")]),_vm._v("13"),_c('span',{pre:true,attrs:{"class":"token punctuation"}},[_vm._v("-")]),_vm._v("alpine\n    "),_c('span',{pre:true,attrs:{"class":"token key atrule"}},[_vm._v("restart")]),_c('span',{pre:true,attrs:{"class":"token punctuation"}},[_vm._v(":")]),_vm._v(" always\n    "),_c('span',{pre:true,attrs:{"class":"token key atrule"}},[_vm._v("networks")]),_c('span',{pre:true,attrs:{"class":"token punctuation"}},[_vm._v(":")]),_vm._v("\n      "),_c('span',{pre:true,attrs:{"class":"token punctuation"}},[_vm._v("-")]),_vm._v(" cassandre\n    "),_c('span',{pre:true,attrs:{"class":"token key atrule"}},[_vm._v("volumes")]),_c('span',{pre:true,attrs:{"class":"token punctuation"}},[_vm._v(":")]),_vm._v("\n      "),_c('span',{pre:true,attrs:{"class":"token punctuation"}},[_vm._v("-")]),_vm._v(" cassandre_database"),_c('span',{pre:true,attrs:{"class":"token punctuation"}},[_vm._v(":")]),_vm._v("/var/lib/postgresql/data\n    "),_c('span',{pre:true,attrs:{"class":"token key atrule"}},[_vm._v("environment")]),_c('span',{pre:true,attrs:{"class":"token punctuation"}},[_vm._v(":")]),_vm._v("\n      "),_c('span',{pre:true,attrs:{"class":"token punctuation"}},[_vm._v("-")]),_vm._v(" TZ=Europe/Paris\n      "),_c('span',{pre:true,attrs:{"class":"token punctuation"}},[_vm._v("-")]),_vm._v(" PGTZ=Europe/Paris\n      "),_c('span',{pre:true,attrs:{"class":"token punctuation"}},[_vm._v("-")]),_vm._v(" POSTGRES_DB=cassandre_trading_bot\n      "),_c('span',{pre:true,attrs:{"class":"token punctuation"}},[_vm._v("-")]),_vm._v(" POSTGRES_USER=cassandre_trading_bot\n      "),_c('span',{pre:true,attrs:{"class":"token punctuation"}},[_vm._v("-")]),_vm._v(" POSTGRES_PASSWORD=mypassword\n")])])]),_c('p',[_vm._v("This starts a Postgresql image where our trading bot will store its data (strategies, orders, trades & positions).")]),_vm._v(" "),_c('h3',{attrs:{"id":"postgresql-backup"}},[_c('a',{staticClass:"header-anchor",attrs:{"href":"#postgresql-backup"}},[_vm._v("#")]),_vm._v(" Postgresql backup")]),_vm._v(" "),_c('div',{staticClass:"language-yaml extra-class"},[_c('pre',{pre:true,attrs:{"class":"language-yaml"}},[_c('code',[_vm._v("  "),_c('span',{pre:true,attrs:{"class":"token key atrule"}},[_vm._v("cassandre-postgresql-backup")]),_c('span',{pre:true,attrs:{"class":"token punctuation"}},[_vm._v(":")]),_vm._v("\n    "),_c('span',{pre:true,attrs:{"class":"token key atrule"}},[_vm._v("image")]),_c('span',{pre:true,attrs:{"class":"token punctuation"}},[_vm._v(":")]),_vm._v(" prodrigestivill/postgres"),_c('span',{pre:true,attrs:{"class":"token punctuation"}},[_vm._v("-")]),_vm._v("backup"),_c('span',{pre:true,attrs:{"class":"token punctuation"}},[_vm._v("-")]),_vm._v("local"),_c('span',{pre:true,attrs:{"class":"token punctuation"}},[_vm._v(":")]),_vm._v("13"),_c('span',{pre:true,attrs:{"class":"token punctuation"}},[_vm._v("-")]),_vm._v("alpine\n    "),_c('span',{pre:true,attrs:{"class":"token key atrule"}},[_vm._v("depends_on")]),_c('span',{pre:true,attrs:{"class":"token punctuation"}},[_vm._v(":")]),_vm._v("\n      "),_c('span',{pre:true,attrs:{"class":"token punctuation"}},[_vm._v("-")]),_vm._v(" cassandre"),_c('span',{pre:true,attrs:{"class":"token punctuation"}},[_vm._v("-")]),_vm._v("postgresql\n    "),_c('span',{pre:true,attrs:{"class":"token key atrule"}},[_vm._v("restart")]),_c('span',{pre:true,attrs:{"class":"token punctuation"}},[_vm._v(":")]),_vm._v(" always\n    "),_c('span',{pre:true,attrs:{"class":"token key atrule"}},[_vm._v("networks")]),_c('span',{pre:true,attrs:{"class":"token punctuation"}},[_vm._v(":")]),_vm._v("\n      "),_c('span',{pre:true,attrs:{"class":"token punctuation"}},[_vm._v("-")]),_vm._v(" cassandre\n    "),_c('span',{pre:true,attrs:{"class":"token key atrule"}},[_vm._v("volumes")]),_c('span',{pre:true,attrs:{"class":"token punctuation"}},[_vm._v(":")]),_vm._v("\n      "),_c('span',{pre:true,attrs:{"class":"token punctuation"}},[_vm._v("-")]),_vm._v(" cassandre_database_backup"),_c('span',{pre:true,attrs:{"class":"token punctuation"}},[_vm._v(":")]),_vm._v("/backups\n    "),_c('span',{pre:true,attrs:{"class":"token key atrule"}},[_vm._v("environment")]),_c('span',{pre:true,attrs:{"class":"token punctuation"}},[_vm._v(":")]),_vm._v("\n      "),_c('span',{pre:true,attrs:{"class":"token punctuation"}},[_vm._v("-")]),_vm._v(" TZ=Europe/Paris\n      "),_c('span',{pre:true,attrs:{"class":"token punctuation"}},[_vm._v("-")]),_vm._v(" POSTGRES_HOST=postgresql\n      "),_c('span',{pre:true,attrs:{"class":"token punctuation"}},[_vm._v("-")]),_vm._v(" POSTGRES_DB=cassandre_trading_bot\n      "),_c('span',{pre:true,attrs:{"class":"token punctuation"}},[_vm._v("-")]),_vm._v(" POSTGRES_USER=cassandre_trading_bot\n      "),_c('span',{pre:true,attrs:{"class":"token punctuation"}},[_vm._v("-")]),_vm._v(" POSTGRES_PASSWORD=mypassword\n      "),_c('span',{pre:true,attrs:{"class":"token punctuation"}},[_vm._v("-")]),_vm._v(" POSTGRES_EXTRA_OPTS="),_c('span',{pre:true,attrs:{"class":"token punctuation"}},[_vm._v("-")]),_c('span',{pre:true,attrs:{"class":"token punctuation"}},[_vm._v("-")]),_vm._v("schema=public\n      "),_c('span',{pre:true,attrs:{"class":"token punctuation"}},[_vm._v("-")]),_vm._v(" SCHEDULE=@hourly\n      "),_c('span',{pre:true,attrs:{"class":"token punctuation"}},[_vm._v("-")]),_vm._v(" BACKUP_KEEP_DAYS=7\n      "),_c('span',{pre:true,attrs:{"class":"token punctuation"}},[_vm._v("-")]),_vm._v(" BACKUP_KEEP_WEEKS=4\n      "),_c('span',{pre:true,attrs:{"class":"token punctuation"}},[_vm._v("-")]),_vm._v(" BACKUP_KEEP_MONTHS=0\n      "),_c('span',{pre:true,attrs:{"class":"token punctuation"}},[_vm._v("-")]),_vm._v(" HEALTHCHECK_PORT=8080\n")])])]),_c('p',[_vm._v("This starts an image that will connect to the Postgresql image and make backups according to the parameters: "),_c('code',[_vm._v("SCHEDULE")]),_vm._v(", "),_c('code',[_vm._v("BACKUP_KEEP_DAYS")]),_vm._v(", "),_c('code',[_vm._v("BACKUP_KEEP_WEEKS")]),_vm._v(" and "),_c('code',[_vm._v("BACKUP_KEEP_MONTHS")]),_vm._v(".")]),_vm._v(" "),_c('h2',{attrs:{"id":"your-bot"}},[_c('a',{staticClass:"header-anchor",attrs:{"href":"#your-bot"}},[_vm._v("#")]),_vm._v(" Your bot")]),_vm._v(" "),_c('p',[_vm._v("There are several ways to do what we are trying to do, we choose this one:")]),_vm._v(" "),_c('ul',[_c('li',[_vm._v("Our trading bot source code is hosted in a private "),_c('a',{attrs:{"href":"https://github.com/","target":"_blank","rel":"noopener noreferrer"}},[_vm._v("Github"),_c('OutboundLink')],1),_vm._v(" project.")]),_vm._v(" "),_c('li',[_vm._v("On every push, our "),_c('a',{attrs:{"href":"https://github.com/features/actions","target":"_blank","rel":"noopener noreferrer"}},[_vm._v("Github actions"),_c('OutboundLink')],1),_vm._v(" script does the following steps:\n"),_c('ul',[_c('li',[_vm._v("Creates the docker image of our trading bot.")]),_vm._v(" "),_c('li',[_vm._v("Login to our "),_c('a',{attrs:{"href":"https://hub.docker.com/","target":"_blank","rel":"noopener noreferrer"}},[_vm._v("docker hub repository"),_c('OutboundLink')],1),_vm._v(".")]),_vm._v(" "),_c('li',[_vm._v("Push the image to our docker hub repository.")]),_vm._v(" "),_c('li',[_vm._v("Connect to our private server via ssh.")]),_vm._v(" "),_c('li',[_vm._v("Stop the previous running image of our bot and download/run the new image.")])])])]),_vm._v(" "),_c('p',[_vm._v("The source of our script is "),_c('a',{attrs:{"href":"https://raw.githubusercontent.com/cassandre-tech/cassandre-trading-bot/development/trading-bot-server/deployment.yml","target":"_blank","rel":"noopener noreferrer"}},[_vm._v("here"),_c('OutboundLink')],1),_vm._v(" and this is what it does:")]),_vm._v(" "),_c('h3',{attrs:{"id":"build-the-docker-image"}},[_c('a',{staticClass:"header-anchor",attrs:{"href":"#build-the-docker-image"}},[_vm._v("#")]),_vm._v(" Build the docker image")]),_vm._v(" "),_c('div',{staticClass:"language-yaml extra-class"},[_c('pre',{pre:true,attrs:{"class":"language-yaml"}},[_c('code',[_c('span',{pre:true,attrs:{"class":"token punctuation"}},[_vm._v("-")]),_vm._v(" "),_c('span',{pre:true,attrs:{"class":"token key atrule"}},[_vm._v("name")]),_c('span',{pre:true,attrs:{"class":"token punctuation"}},[_vm._v(":")]),_vm._v(" Build with Maven and creates the docker image\n  "),_c('span',{pre:true,attrs:{"class":"token key atrule"}},[_vm._v("run")]),_c('span',{pre:true,attrs:{"class":"token punctuation"}},[_vm._v(":")]),_vm._v(" mvn spring"),_c('span',{pre:true,attrs:{"class":"token punctuation"}},[_vm._v("-")]),_vm._v("boot"),_c('span',{pre:true,attrs:{"class":"token punctuation"}},[_vm._v(":")]),_vm._v("build"),_c('span',{pre:true,attrs:{"class":"token punctuation"}},[_vm._v("-")]),_vm._v("image\n")])])]),_c('h3',{attrs:{"id":"push-image-to-our-private-docker-hub"}},[_c('a',{staticClass:"header-anchor",attrs:{"href":"#push-image-to-our-private-docker-hub"}},[_vm._v("#")]),_vm._v(" Push image to our private docker hub")]),_vm._v(" "),_c('div',{staticClass:"language-yaml extra-class"},[_c('pre',{pre:true,attrs:{"class":"language-yaml"}},[_c('code',[_c('span',{pre:true,attrs:{"class":"token punctuation"}},[_vm._v("-")]),_vm._v(" "),_c('span',{pre:true,attrs:{"class":"token key atrule"}},[_vm._v("name")]),_c('span',{pre:true,attrs:{"class":"token punctuation"}},[_vm._v(":")]),_vm._v(" Push image to docker hub\n  "),_c('span',{pre:true,attrs:{"class":"token key atrule"}},[_vm._v("run")]),_c('span',{pre:true,attrs:{"class":"token punctuation"}},[_vm._v(":")]),_vm._v(" "),_c('span',{pre:true,attrs:{"class":"token punctuation"}},[_vm._v("|")]),_c('span',{pre:true,attrs:{"class":"token scalar string"}},[_vm._v("\n    echo ${{ secrets.DOCKER_HUB_PASSWORD }} | docker login -u ${{ secrets.DOCKER_HUB_USERNAME }} --password-stdin\n    docker push straumat/trading-bot:latest")]),_vm._v("\n")])])]),_c('h3',{attrs:{"id":"deploy-to-the-production-server"}},[_c('a',{staticClass:"header-anchor",attrs:{"href":"#deploy-to-the-production-server"}},[_vm._v("#")]),_vm._v(" Deploy to the production server")]),_vm._v(" "),_c('p',[_vm._v("The CI script does the following:")]),_vm._v(" "),_c('ul',[_c('li',[_vm._v("Connect to our production server with SSH.")]),_vm._v(" "),_c('li',[_vm._v("Login to our docker private account.")]),_vm._v(" "),_c('li',[_vm._v("Stop & delete the image of the previous trading bot (if it exists).")]),_vm._v(" "),_c('li',[_vm._v("Retrieve the new image from the docker hub.")]),_vm._v(" "),_c('li',[_vm._v("Run the image with all the parameters specified in Github secrets.")])]),_vm._v(" "),_c('div',{staticClass:"language-yaml extra-class"},[_c('pre',{pre:true,attrs:{"class":"language-yaml"}},[_c('code',[_c('span',{pre:true,attrs:{"class":"token punctuation"}},[_vm._v("-")]),_vm._v(" "),_c('span',{pre:true,attrs:{"class":"token key atrule"}},[_vm._v("name")]),_c('span',{pre:true,attrs:{"class":"token punctuation"}},[_vm._v(":")]),_vm._v(" Deploy to production server\n  "),_c('span',{pre:true,attrs:{"class":"token key atrule"}},[_vm._v("uses")]),_c('span',{pre:true,attrs:{"class":"token punctuation"}},[_vm._v(":")]),_vm._v(" appleboy/ssh"),_c('span',{pre:true,attrs:{"class":"token punctuation"}},[_vm._v("-")]),_vm._v("action@master\n  "),_c('span',{pre:true,attrs:{"class":"token key atrule"}},[_vm._v("with")]),_c('span',{pre:true,attrs:{"class":"token punctuation"}},[_vm._v(":")]),_vm._v("\n    "),_c('span',{pre:true,attrs:{"class":"token key atrule"}},[_vm._v("host")]),_c('span',{pre:true,attrs:{"class":"token punctuation"}},[_vm._v(":")]),_vm._v(" $"),_c('span',{pre:true,attrs:{"class":"token punctuation"}},[_vm._v("{")]),_c('span',{pre:true,attrs:{"class":"token punctuation"}},[_vm._v("{")]),_vm._v(" secrets.SSH_HOST "),_c('span',{pre:true,attrs:{"class":"token punctuation"}},[_vm._v("}")]),_c('span',{pre:true,attrs:{"class":"token punctuation"}},[_vm._v("}")]),_vm._v("\n    "),_c('span',{pre:true,attrs:{"class":"token key atrule"}},[_vm._v("port")]),_c('span',{pre:true,attrs:{"class":"token punctuation"}},[_vm._v(":")]),_vm._v(" $"),_c('span',{pre:true,attrs:{"class":"token punctuation"}},[_vm._v("{")]),_c('span',{pre:true,attrs:{"class":"token punctuation"}},[_vm._v("{")]),_vm._v(" secrets.SSH_PORT "),_c('span',{pre:true,attrs:{"class":"token punctuation"}},[_vm._v("}")]),_c('span',{pre:true,attrs:{"class":"token punctuation"}},[_vm._v("}")]),_vm._v("\n    "),_c('span',{pre:true,attrs:{"class":"token key atrule"}},[_vm._v("username")]),_c('span',{pre:true,attrs:{"class":"token punctuation"}},[_vm._v(":")]),_vm._v(" $"),_c('span',{pre:true,attrs:{"class":"token punctuation"}},[_vm._v("{")]),_c('span',{pre:true,attrs:{"class":"token punctuation"}},[_vm._v("{")]),_vm._v(" secrets.SSH_USERNAME "),_c('span',{pre:true,attrs:{"class":"token punctuation"}},[_vm._v("}")]),_c('span',{pre:true,attrs:{"class":"token punctuation"}},[_vm._v("}")]),_vm._v("\n    "),_c('span',{pre:true,attrs:{"class":"token key atrule"}},[_vm._v("password")]),_c('span',{pre:true,attrs:{"class":"token punctuation"}},[_vm._v(":")]),_vm._v(" $"),_c('span',{pre:true,attrs:{"class":"token punctuation"}},[_vm._v("{")]),_c('span',{pre:true,attrs:{"class":"token punctuation"}},[_vm._v("{")]),_vm._v(" secrets.SSH_PASSWORD "),_c('span',{pre:true,attrs:{"class":"token punctuation"}},[_vm._v("}")]),_c('span',{pre:true,attrs:{"class":"token punctuation"}},[_vm._v("}")]),_vm._v("\n    "),_c('span',{pre:true,attrs:{"class":"token key atrule"}},[_vm._v("script")]),_c('span',{pre:true,attrs:{"class":"token punctuation"}},[_vm._v(":")]),_vm._v(" "),_c('span',{pre:true,attrs:{"class":"token punctuation"}},[_vm._v("|")]),_c('span',{pre:true,attrs:{"class":"token scalar string"}},[_vm._v("\n      echo ${{ secrets.DOCKER_HUB_PASSWORD }} | docker login -u ${{ secrets.DOCKER_HUB_USERNAME }} --password-stdin\n      docker stop $(docker ps -aq --filter \"label=trading-bot\")\n      docker rm -f $(docker ps -aq --filter \"label=trading-bot\")\n      docker pull straumat/trading-bot:latest\n      docker run  -d \\\n                  --security-opt apparmor=unconfined \\\n                  --network=\"cassandre\" \\\n                  -e TZ=Europe/Paris \\\n                  -e CASSANDRE_TRADING_BOT_EXCHANGE_NAME='${{ secrets.CASSANDRE_TRADING_BOT_EXCHANGE_NAME }}' \\\n                  -e CASSANDRE_TRADING_BOT_EXCHANGE_USERNAME='${{ secrets.CASSANDRE_TRADING_BOT_EXCHANGE_USERNAME }}' \\\n                  -e CASSANDRE_TRADING_BOT_EXCHANGE_PASSPHRASE='${{ secrets.CASSANDRE_TRADING_BOT_EXCHANGE_PASSPHRASE }}' \\\n                  -e CASSANDRE_TRADING_BOT_EXCHANGE_KEY='${{ secrets.CASSANDRE_TRADING_BOT_EXCHANGE_KEY }}' \\\n                  -e CASSANDRE_TRADING_BOT_EXCHANGE_SECRET='${{ secrets.CASSANDRE_TRADING_BOT_EXCHANGE_SECRET }}' \\\n                  -e CASSANDRE_TRADING_BOT_EXCHANGE_MODES_SANDBOX='${{ secrets.CASSANDRE_TRADING_BOT_EXCHANGE_MODES_SANDBOX }}' \\\n                  -e CASSANDRE_TRADING_BOT_EXCHANGE_MODES_DRY='${{ secrets.CASSANDRE_TRADING_BOT_EXCHANGE_MODES_DRY }}' \\\n                  -e CASSANDRE_TRADING_BOT_EXCHANGE_RATES_ACCOUNT='${{ secrets.CASSANDRE_TRADING_BOT_EXCHANGE_RATES_ACCOUNT }}' \\\n                  -e CASSANDRE_TRADING_BOT_EXCHANGE_RATES_TICKER='${{ secrets.CASSANDRE_TRADING_BOT_EXCHANGE_RATES_TICKER }}' \\\n                  -e CASSANDRE_TRADING_BOT_EXCHANGE_RATES_ORDER='${{ secrets.CASSANDRE_TRADING_BOT_EXCHANGE_RATES_ORDER }}' \\\n                  -e CASSANDRE_TRADING_BOT_DATABASE_DATASOURCE_DRIVER-CLASS-NAME=${{ secrets.CASSANDRE_TRADING_BOT_DATABASE_DATASOURCE_DRIVER_CLASS_NAME }} \\\n                  -e CASSANDRE_TRADING_BOT_DATABASE_DATASOURCE_URL=${{ secrets.CASSANDRE_TRADING_BOT_DATABASE_DATASOURCE_URL }} \\\n                  -e CASSANDRE_TRADING_BOT_DATABASE_DATASOURCE_USERNAME=${{ secrets.CASSANDRE_TRADING_BOT_DATABASE_DATASOURCE_USERNAME }} \\\n                  -e CASSANDRE_TRADING_BOT_DATABASE_DATASOURCE_PASSWORD=${{ secrets.CASSANDRE_TRADING_BOT_DATABASE_DATASOURCE_PASSWORD }} \\\n                  -e CASSANDRE_TRADING_BOT_DATABASE_TABLE-PREFIX=${{ secrets.CASSANDRE_TRADING_BOT_DATABASE_TABLE_PREFIX }} \\\n                  -l trading-bot \\\n                  straumat/trading-bot:latest")]),_vm._v("\n")])])]),_c('p',[_vm._v("These are the parameters for the Postgresql connection:")]),_vm._v(" "),_c('table',[_c('thead',[_c('tr',[_c('th',{staticStyle:{"text-align":"left"}},[_vm._v("Parameter")]),_vm._v(" "),_c('th',{staticStyle:{"text-align":"left"}},[_vm._v("Value")])])]),_vm._v(" "),_c('tbody',[_c('tr',[_c('td',{staticStyle:{"text-align":"left"}},[_vm._v("DRIVER-CLASS-NAME")]),_vm._v(" "),_c('td',{staticStyle:{"text-align":"left"}},[_vm._v("org.postgresql.Driver")])]),_vm._v(" "),_c('tr',[_c('td',{staticStyle:{"text-align":"left"}},[_vm._v("URL")]),_vm._v(" "),_c('td',{staticStyle:{"text-align":"left"}},[_vm._v("jdbc:postgresql://cassandre-postgresql/cassandre_trading_bot")])]),_vm._v(" "),_c('tr',[_c('td',{staticStyle:{"text-align":"left"}},[_vm._v("USERNAME")]),_vm._v(" "),_c('td',{staticStyle:{"text-align":"left"}},[_vm._v("cassandre_trading_bot")])]),_vm._v(" "),_c('tr',[_c('td',{staticStyle:{"text-align":"left"}},[_vm._v("PASSWORD")]),_vm._v(" "),_c('td',{staticStyle:{"text-align":"left"}},[_vm._v("mypassword")])])])]),_vm._v(" "),_c('div',{staticClass:"custom-block tip"},[_c('p',{staticClass:"custom-block-title"},[_vm._v("TIP")]),_vm._v(" "),_c('p',[_vm._v("On the server, thanks to the docker label, you can view the bot logs with the command : "),_c('code',[_vm._v("docker logs $(docker ps -aq --filter \"label=trading-bot\") --follow")])])])])}
var staticRenderFns = []



/***/ }),

/***/ "./src/learn/deploy-and-run/docker.md":
/*!********************************************!*\
  !*** ./src/learn/deploy-and-run/docker.md ***!
  \********************************************/
/*! exports provided: default */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony import */ var _docker_md_vue_type_template_id_5629d19a___WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./docker.md?vue&type=template&id=5629d19a& */ "./src/learn/deploy-and-run/docker.md?vue&type=template&id=5629d19a&");
/* harmony import */ var _config_yarn_global_node_modules_vue_loader_lib_runtime_componentNormalizer_js__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ../../../../../../../.config/yarn/global/node_modules/vue-loader/lib/runtime/componentNormalizer.js */ "../../../../.config/yarn/global/node_modules/vue-loader/lib/runtime/componentNormalizer.js");

var script = {}


/* normalize component */

var component = Object(_config_yarn_global_node_modules_vue_loader_lib_runtime_componentNormalizer_js__WEBPACK_IMPORTED_MODULE_1__["default"])(
  script,
  _docker_md_vue_type_template_id_5629d19a___WEBPACK_IMPORTED_MODULE_0__["render"],
  _docker_md_vue_type_template_id_5629d19a___WEBPACK_IMPORTED_MODULE_0__["staticRenderFns"],
  false,
  null,
  null,
  null
  
)

/* harmony default export */ __webpack_exports__["default"] = (component.exports);

/***/ }),

/***/ "./src/learn/deploy-and-run/docker.md?vue&type=template&id=5629d19a&":
/*!***************************************************************************!*\
  !*** ./src/learn/deploy-and-run/docker.md?vue&type=template&id=5629d19a& ***!
  \***************************************************************************/
/*! exports provided: render, staticRenderFns */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony import */ var _config_yarn_global_node_modules_cache_loader_dist_cjs_js_cacheDirectory_config_yarn_global_node_modules_vuepress_core_node_modules_cache_vuepress_cacheIdentifier_4b61893c_vue_loader_template_config_yarn_global_node_modules_vue_loader_lib_loaders_templateLoader_js_vue_loader_options_config_yarn_global_node_modules_cache_loader_dist_cjs_js_ref_1_0_config_yarn_global_node_modules_vue_loader_lib_index_js_ref_1_1_config_yarn_global_node_modules_vuepress_markdown_loader_index_js_ref_1_2_docker_md_vue_type_template_id_5629d19a___WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! -!../../../../../../../.config/yarn/global/node_modules/cache-loader/dist/cjs.js?{"cacheDirectory":"../../../../.config/yarn/global/node_modules/@vuepress/core/node_modules/.cache/vuepress","cacheIdentifier":"4b61893c-vue-loader-template"}!../../../../../../../.config/yarn/global/node_modules/vue-loader/lib/loaders/templateLoader.js??vue-loader-options!../../../../../../../.config/yarn/global/node_modules/cache-loader/dist/cjs.js??ref--1-0!../../../../../../../.config/yarn/global/node_modules/vue-loader/lib??ref--1-1!../../../../../../../.config/yarn/global/node_modules/@vuepress/markdown-loader??ref--1-2!./docker.md?vue&type=template&id=5629d19a& */ "../../../../.config/yarn/global/node_modules/cache-loader/dist/cjs.js?{\"cacheDirectory\":\"../../../../.config/yarn/global/node_modules/@vuepress/core/node_modules/.cache/vuepress\",\"cacheIdentifier\":\"4b61893c-vue-loader-template\"}!../../../../.config/yarn/global/node_modules/vue-loader/lib/loaders/templateLoader.js?!../../../../.config/yarn/global/node_modules/cache-loader/dist/cjs.js?!../../../../.config/yarn/global/node_modules/vue-loader/lib/index.js?!../../../../.config/yarn/global/node_modules/@vuepress/markdown-loader/index.js?!./src/learn/deploy-and-run/docker.md?vue&type=template&id=5629d19a&");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "render", function() { return _config_yarn_global_node_modules_cache_loader_dist_cjs_js_cacheDirectory_config_yarn_global_node_modules_vuepress_core_node_modules_cache_vuepress_cacheIdentifier_4b61893c_vue_loader_template_config_yarn_global_node_modules_vue_loader_lib_loaders_templateLoader_js_vue_loader_options_config_yarn_global_node_modules_cache_loader_dist_cjs_js_ref_1_0_config_yarn_global_node_modules_vue_loader_lib_index_js_ref_1_1_config_yarn_global_node_modules_vuepress_markdown_loader_index_js_ref_1_2_docker_md_vue_type_template_id_5629d19a___WEBPACK_IMPORTED_MODULE_0__["render"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "staticRenderFns", function() { return _config_yarn_global_node_modules_cache_loader_dist_cjs_js_cacheDirectory_config_yarn_global_node_modules_vuepress_core_node_modules_cache_vuepress_cacheIdentifier_4b61893c_vue_loader_template_config_yarn_global_node_modules_vue_loader_lib_loaders_templateLoader_js_vue_loader_options_config_yarn_global_node_modules_cache_loader_dist_cjs_js_ref_1_0_config_yarn_global_node_modules_vue_loader_lib_index_js_ref_1_1_config_yarn_global_node_modules_vuepress_markdown_loader_index_js_ref_1_2_docker_md_vue_type_template_id_5629d19a___WEBPACK_IMPORTED_MODULE_0__["staticRenderFns"]; });



/***/ })

}]);
//# sourceMappingURL=12.f2a9da01.js.map