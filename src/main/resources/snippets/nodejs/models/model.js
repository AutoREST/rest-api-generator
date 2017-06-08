var mongoose = require('mongoose'),
	integerValidator = require('mongoose-integer'),
	Schema = mongoose.Schema;

var {{model_name}}Schema = new Schema({
	{{fields}}
	{{type_field}}
    // attribute8: { type: [Number], integer: true },
    // attribute9: {
    //     type: Date,
    // },
    // attribute10: {
    //     type: Object,
    // },
    // attribute11: {
    //     type: String,
    //     match: /^[a-zA-Z]*$/,
    // }
});

{{multikey_unique_index}}
{{id_virtual}}
{{model_name}}Schema.plugin(integerValidator);

{{model_name}}Schema.methods.cleanObject = function(baseUrl, depth) {
	var doc = this.toObject({ virtuals: true });
	var navLinks = (depth == 0);
	var itemLinks = (depth == 1);

	delete doc.__v;
	delete doc._id;
	delete doc.id;
	{{delete_type}}

	if(navLinks || itemLinks){
{{ref_resources_props}}
{{props_hyperlinks}}
		if(itemLinks){
{{ref_resources_array_items}}
		}else {
{{ref_resources_array_hyperlinks}}
		}
	}
	return doc;
};

module.exports = mongoose.model('{{model_name}}', {{model_name}}Schema, '{{collection_name}}');
