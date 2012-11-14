package rundeck

import com.fasterxml.jackson.databind.ObjectMapper

class PluginStep extends WorkflowStep{
    Boolean nodeStep
    String type
    String jsonData
    static constraints = {
        nodeStep nullable: false, blank: false
        type nullable: false, blank: false
        jsonData(nullable: true, blank: true)
    }
    //ignore fake property 'configuration' and do not store it
    static transients = ['configuration']

    public Map getConfiguration() {
        //de-serialize the json
        if (null != jsonData) {
            final ObjectMapper mapper = new ObjectMapper()
            return mapper.readValue(jsonData, Map.class)
        } else {
            return null
        }

    }

    public void setConfiguration(Map obj) {
        //serialize json and store into field
        if (null != obj) {
            final ObjectMapper mapper = new ObjectMapper()
            jsonData = mapper.writeValueAsString(obj)
        } else {
            jsonData = null
        }
    }

    static mapping = {
        jsonData(type: 'text')
    }

    public Map toMap() {
        [type: type, nodeStep:nodeStep,configuration: this.configuration]
    }

    public PluginStep createClone() {
        return new PluginStep(type: type, nodeStep: nodeStep, jsonData: jsonData)
    }

    @Override
    public String toString() {
        return "PluginStep{" +
               "nodeStep=" + nodeStep +
               ", type='" + type + '\'' +
               ", jsonData='" + jsonData + '\'' +
               '}';
    }

    @Override
    public String summarize() {
        return "Plugin["+ type + ', nodeStep: '+nodeStep+']';
    }
}
