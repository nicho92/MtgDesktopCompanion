<#list modele as alert>
    <#if (alert.offers?size>0)>
     ${alert.card.name} (${alert.card.edition.id}) : ${alert.offers?size} offers <= ${alert.price}
    </#if>
</#list>