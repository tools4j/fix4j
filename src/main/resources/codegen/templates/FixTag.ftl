<@pp.dropOutputFile />
<#list fields?keys as versionString>
<#assign version=versionString?substring(3)>
<#assign doc=fields[versionString]>
<@pp.changeOutputFile name=pp.home + "org/fix4j/engine/fix/" + versionString + "/FixTag" + version + ".java" />
package org.fixj.engine.fix.${versionString};

import org.fix4j.engine.tag.*;
import org.fix4j.engine.tag.impl.*;

public final class FixTag${version} {
<#list doc.Fields.Field as field>
	<#if field.Type=='String'>
	public static final StringTag ${field.Name} = new BasicStringTag("${field.Name}", ${field.Tag});
	<#elseif field.Type=='char'>
	public static final CharTag ${field.Name} = new BasicCharTag("${field.Name}", ${field.Tag});
	<#elseif field.Type=='int'>
	public static final IntTag ${field.Name} = new BasicIntTag("${field.Name}", ${field.Tag});
	<#elseif field.Type=='float' || field.Type=='Price' || field.Type=='Amt' || field.Type=='Qty'>
	public static final DoubleTag ${field.Name} = new BasicDoubleTag("${field.Name}", ${field.Tag});
	<#else>
	public static final FixTag ${field.Name} = null;
	</#if>
</#list>
}

</#list>