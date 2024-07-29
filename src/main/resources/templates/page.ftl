import 'package:flutter/material.dart';
<#if useGoRouter>
import 'package:go_router/go_router.dart';
</#if>
import './${controllerFile}';

class ${pageClassName} extends StatefulWidget {
    static const name = "${pageClassName}";

    <#if useGoRouter>
    static final GoRoute route = GoRoute(
      path: name,
      name: name,
      builder: (context, state) {
        return const ${pageClassName}();
      },
    );

    static Future navigate(BuildContext context) {
        return context.pushNamed(name);
    }
    </#if>

    const ${pageClassName}({super.key});

    @override
    State<${pageClassName}> createState() => _${pageClassName}State();
}

class _${pageClassName}State extends State<${pageClassName}> {

    final ${controllerClassName} _controller = ${controllerClassName}();

    @override
    void initState() {
      super.initState();
    }

    @override
     Widget build(BuildContext context) {
        return Scaffold(
          appBar: AppBar(

          ),
          body: Column(

          ),
        );
     }
}