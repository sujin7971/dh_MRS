const {merge} = require('webpack-merge');
const path = require('path');
const resourcesPath = 'src/main/webapp/resources';
const commonBasePath = 'core-assets';
const commonConfig = {
	mode: 'production',
	resolve: {
		alias: {
			'/resources': path.resolve(__dirname, resourcesPath),
		}
	},
	module: {
		rules: [
			{
				test: /\.css$/i,
				use: ['style-loader', 'css-loader'],
			},
		],
	},
};

const createConfig = (entryFile) => {
	const entryName = path.basename(entryFile, '.js');
	const outputDir = path.join(path.dirname(entryFile), 'dist');

	return Object.assign({}, commonConfig, {
		entry: path.resolve(__dirname, resourcesPath, commonBasePath, entryFile),
		output: {
			filename: `${entryName}.bundle.js`,
			path: path.resolve(__dirname, resourcesPath, commonBasePath, outputDir)
		}
	});
};

const modal_module = merge(createConfig('essential/lime-modal/index.js'), {
	output: {
    	library: 'ModalService'
  	}
});

module.exports = modal_module;