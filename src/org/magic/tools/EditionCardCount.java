package org.magic.tools;

import java.util.HashMap;
import java.util.Map;

import org.magic.api.beans.MagicEdition;

public class EditionCardCount {

	Map<String,Integer> map;
	
	private static EditionCardCount inst;
	
	public static EditionCardCount getInstance()
	{
		if(inst==null)
			inst=new EditionCardCount();
		
		return inst;
	}
	
	public Integer getCardCount(MagicEdition ed)
	{
		return map.get(ed.getId());
	}
	
	public Integer getCardCount(String ed)
	{
		return map.get(ed);
	}
	
	
	private EditionCardCount() {
		map=new HashMap<String, Integer>();
		map.put("LEA",295);
		map.put("LEB",302);
		map.put("ARN",92);
		map.put("2ED",302);
		map.put("CED",302);
		map.put("CEI",302);
		map.put("pDRC",1);
		map.put("ATQ",100);
		map.put("3ED",306);
		map.put("LEG",310);
		map.put("DRK",119);
		map.put("pMEI",148);
		map.put("FEM",187);
		map.put("pLGM",2);
		map.put("4ED",378);
		map.put("ICE",383);
		map.put("CHR",125);
		map.put("HML",140);
		map.put("ALL",199);
		map.put("RQS",54);
		map.put("pARL",83);
		map.put("pCEL",6);
		map.put("MIR",350);
		map.put("MGB",10);
		map.put("ITP",57);
		map.put("VIS",167);
		map.put("5ED",449);
		map.put("pPOD",6);
		map.put("POR",222);
		map.put("VAN",116);
		map.put("WTH",167);
		map.put("pPRE",307);
		map.put("TMP",350);
		map.put("STH",143);
		map.put("PO2",165);
		map.put("pJGP",100);
		map.put("EXO",143);
		map.put("UGL",94);
		map.put("pALP",15);
		map.put("USG",350);
		map.put("ATH",81);
		map.put("ULG",143);
		map.put("6ED",350);
		map.put("PTK",180);
		map.put("UDS",143);
		map.put("S99",173);
		map.put("pGRU",5);
		map.put("pWOR",1);
		map.put("pWOS",1);
		map.put("MMQ",350);
		map.put("BRB",136);
		map.put("pSUS",32);
		map.put("pFNM",193);
		map.put("pELP",15);
		map.put("NMS",143);
		map.put("S00",58);
		map.put("PCY",143);
		map.put("BTD",90);
		map.put("INV",355);
		map.put("PLS",146);
		map.put("7ED",350);
		map.put("pMPR",53);
		map.put("APC",148);
		map.put("ODY",350);
		map.put("DKM",56);
		map.put("TOR",143);
		map.put("JUD",143);
		map.put("ONS",350);
		map.put("LGN",145);
		map.put("SCG",143);
		map.put("pREL",14);
		map.put("8ED",357);
		map.put("MRD",306);
		map.put("DST",165);
		map.put("5DN",165);
		map.put("CHK",317);
		map.put("UNH",145);
		map.put("BOK",170);
		map.put("SOK",170);
		map.put("9ED",359);
		map.put("RAV",306);
		map.put("p2HG",1);
		map.put("pGTW",20);
		map.put("GPT",165);
		map.put("pCMP",12);
		map.put("DIS",190);
		map.put("CSP",155);
		map.put("CST",62);
		map.put("TSP",301);
		map.put("TSB",122);
		map.put("pHHO",10);
		map.put("PLC",168);
		map.put("pPRO",5);
		map.put("pGPX",11);
		map.put("FUT",180);
		map.put("10E",383);
		map.put("pMGD",50);
		map.put("MED",195);
		map.put("LRW",301);
		map.put("EVG",65);
		map.put("pLPA",33);
		map.put("MOR",150);
		map.put("p15A",2);
		map.put("SHM",301);
		map.put("pSUM",2);
		map.put("EVE",180);
		map.put("DRB",15);
		map.put("ME2",245);
		map.put("pWPN",45);
		map.put("ALA",249);
		map.put("DD2",63);
		map.put("CON",145);
		map.put("DDC",65);
		map.put("ARB",145);
		map.put("M10",249);
		map.put("V09",15);
		map.put("HOP",212);
		map.put("ME3",230);
		map.put("ZEN",269);
		map.put("DDD",66);
		map.put("H09",41);
		map.put("WWK",145);
		map.put("DDE",74);
		map.put("ROE",248);
		map.put("DPA",113);
		map.put("ARC",196);
		map.put("M11",249);
		map.put("V10",15);
		map.put("DDF",79);
		map.put("SOM",249);
		map.put("PD2",34);
		map.put("ME4",269);
		map.put("MBS",155);
		map.put("DDG",81);
		map.put("NPH",175);
		map.put("CMD",320);
		map.put("M12",249);
		map.put("V11",15);
		map.put("DDH",83);
		map.put("ISD",284);
		map.put("PD3",30);
		map.put("DKA",171);
		map.put("DDI",77);
		map.put("AVR",244);
		map.put("PC2",197);
		map.put("M13",249);
		map.put("V12",15);
		map.put("DDJ",92);
		map.put("RTR",274);
		map.put("CM1",18);
		map.put("GTC",249);
		map.put("DDK",80);
		map.put("pWCQ",2);
		map.put("DGM",171);
		map.put("MMA",229);
		map.put("M14",249);
		map.put("V13",20);
		map.put("DDL",81);
		map.put("THS",249);
		map.put("C13",357);
		map.put("BNG",165);
		map.put("DDM",88);
		map.put("JOU",165);
		map.put("MD1",26);
		map.put("CNS",210);
		map.put("VMA",325);
		map.put("M15",284);
		map.put("CPK",12);
		map.put("V14",15);
		map.put("DDN",82);
		map.put("KTK",269);
		map.put("C14",337);
		map.put("DD3_DVD",62);
		map.put("DD3_EVG",62);
		map.put("DD3_GVL",63);
		map.put("DD3_JVC",62);
		map.put("FRF_UGIN",26);
		map.put("FRF",185);
		map.put("DDO",67);
		map.put("DTK",264);
		map.put("TPR",269);
		map.put("MM2",249);
		map.put("ORI",293);
		map.put("V15",15);
		map.put("DDP",80);
		map.put("BFZ",299);
		map.put("EXP",45);
		map.put("C15",342);
		map.put("OGW",186);
		map.put("DDQ",80);
		map.put("W16",16);
		map.put("SOI",330);
		map.put("EMA",249);
		map.put("EMN",223);
		map.put("V16",15);
		map.put("CN2",221);
	}
	
}
