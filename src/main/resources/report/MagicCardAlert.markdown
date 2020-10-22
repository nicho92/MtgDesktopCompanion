<#list modele as alert>
    <#if (alert.offers?size>0)>
     ${alert.card.name} (${alert.card.editions[0].id}) : ${alert.offers?size} offers <= ${alert.price}
    </#if>
</#list>