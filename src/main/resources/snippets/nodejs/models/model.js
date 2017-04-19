var mongoose = require('mongoose'),
	integerValidator = require('mongoose-integer'),
	Schema = mongoose.Schema;

var {{model_name}}Schema = new Schema({
	{{fields}}
    // _id: {
    //     type: Number,
    //     integer: true,
    //     required: true,
    //     unique: true
    // },
    // attribute1: {
    //     type: Boolean,
    //     required: true
    // },
    // attribute2: {
    //     type: Number,
    //     integer: true,
    //     minimum: 0,
    //     maximum: 255
    // },
    // attribute3: {
    //     type: String,
    //     maxlength: 1,
    //     required: true
    // },
    // attribute4: {
    //     type: Number,
    //     required: true
    // },
    // attribute5: {
    //     type: Number,
    //     required: true
    // },
    // attribute6: {
    //     type: Number,
    //     integer: true,
    //     required: true
    // },
    // attribute7: {
    //     type: Number,
    //     integer: true,
    //     required: true
    // },
    // attribute8: { type: [Number], integer: true },
    // attribute9: {
    //     type: Date,
    //     required: true
    // },
    // attribute10: {
    //     type: Object,
    //     required: true
    // },
    // attribute11: {
    //     type: String,
    //     minlength: 3,
    //     maxlength: 140,
    //     match: /^[a-zA-Z]*$/,
    //     required: true
    // }
});

{{id_virtual}}
{{model_name}}Schema.plugin(integerValidator);

{{model_name}}Schema.methods.cleanObject = function() {
	var doc = this.toObject({ virtuals: true });
	delete doc.__v;
	delete doc._id;
	delete doc.id;
	return doc;
};

{{model_name}}Schema.methods.cleanLinked = function(baseUrl) {
	var doc = this.cleanObject();
{{referenced_resources}}
{{props_hyperlinks}}
	return doc;
};

module.exports = mongoose.model('{{model_name}}', {{model_name}}Schema);
