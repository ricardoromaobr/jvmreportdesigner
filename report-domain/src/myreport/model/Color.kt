package myreport.model

class Color (var r: Float, var g: Float, var b: Float, var a: Float) : ICloneable {
    constructor(r: Float, g: Float, b: Float): this(r, g, b, 1f)

    override fun clone(): Any {
        val c = Color(r,g,b,a)
        return c
    }
}

/*


        sisvg

        passos:

- GESTAO SVG

            orçamento.
            Unidades Superiores -- Cadastra as atividades  nos departamentos


Unidades Superiores

        Unidade Subordinadas (atividades da DP´s)
 */