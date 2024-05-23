module java.decita.api {
    requires static lombok;
    requires org.hamcrest;
    requires org.yaml.snakeyaml;
    exports ru.ewc.decisions.api;
    exports ru.ewc.commands;
    exports ru.ewc.state;
}