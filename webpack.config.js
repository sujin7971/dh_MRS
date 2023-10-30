const {merge} = require('webpack-merge');
const pageModule = require('./webpack.page.config.js');
const partialsModule = require('./webpack.partials.config.js');
const coreModule = require('./webpack.module.config.js');
const merged = [...pageModule, ...partialsModule];

//console.log("pageModule", pageModule);
//console.log("partialsModule", partialsModule);
//console.log("merged", merged);

module.exports = merged;