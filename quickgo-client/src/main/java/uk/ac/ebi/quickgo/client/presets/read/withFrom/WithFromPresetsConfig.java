package uk.ac.ebi.quickgo.client.presets.read.withFrom;

import uk.ac.ebi.quickgo.client.model.presets.CompositePreset;
import uk.ac.ebi.quickgo.client.model.presets.PresetItemBuilder;
import uk.ac.ebi.quickgo.client.presets.read.LogStepListener;
import uk.ac.ebi.quickgo.client.presets.read.PresetsCommonConfig;
import uk.ac.ebi.quickgo.client.presets.read.ff.RawNamedPreset;
import uk.ac.ebi.quickgo.client.presets.read.ff.RawNamedPresetValidator;
import uk.ac.ebi.quickgo.client.presets.read.ff.SourceColumnsFactory;
import uk.ac.ebi.quickgo.client.presets.read.ff.StringToRawNamedPresetMapper;

import java.util.List;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.Resource;

import static uk.ac.ebi.quickgo.client.presets.read.PresetsConfig.SKIP_LIMIT;
import static uk.ac.ebi.quickgo.client.presets.read.PresetsConfigHelper.compositeItemProcessor;
import static uk.ac.ebi.quickgo.client.presets.read.PresetsConfigHelper.fileReader;
import static uk.ac.ebi.quickgo.client.presets.read.PresetsConfigHelper.rawPresetMultiFileReader;
import static uk.ac.ebi.quickgo.client.presets.read.ff.SourceColumnsFactory.Source.DB_COLUMNS;

/**
 * Exposes the {@link Step} bean that is used to read and populate information relating to the with/from preset data.
 *
 * Created 01/09/16
 * @author Edd
 */
@Configuration
@Import({PresetsCommonConfig.class})
public class WithFromPresetsConfig {
    public static final String WITH_FROM_DB_LOADING_STEP_NAME = "WithFromDBReadingStep";
    private static final String DEFAULTS =
            "AGI_LocusCode,AspGD,CGD,CHEBI,EC,ECK,ECO,ECOGENE,EMBL,EchoBASE,EcoliWiki,Ensembl,EnsemblFungi," +
                    "EnsemblPlants,FB,GB,GO,GR,GR_PROTEIN,GR_protein,GenBank,GeneDB,HAMAP,HGNC,IntAct,InterPro," +
                    "JCVI,JCVI_CMR,JCVI_GenProp,KEGG,KEGG_LIGAND,MGI,MaizeGDB,MaizeGDB_Locus,NCBI,NCBI_GP,NCBI_Gene," +
                    "NCBI_gi,PANTHER,PDB,PIR,PR,Pfam,PomBase,PubChem_Compound,PubChem_Substance,RGD,RGDID,RNAcentral," +
                    "RefSeq,SGD,TAIR,TIGR,TIGR_GenProp,UniPathway,UniProt,UniProtKB,UniProtKB,UniProtKB,UniRule," +
                    "WB,ZFIN,dictyBase,protein_id";

    private static final RawNamedPreset INVALID_PRESET = null;

    @Value("#{'${withfrom.db.preset.source:}'.split(',')}")
    private Resource[] resources;
    @Value("${withfrom.db.preset.header.lines:1}")
    private int headerLines;
    @Value("#{'${withfrom.db.preset.defaults:" + DEFAULTS + "}'.split(',')}")
    private List<String> defaults;

    @Bean
    public Step withFromDbStep(
            StepBuilderFactory stepBuilderFactory,
            Integer chunkSize,
            CompositePreset presets) {
        FlatFileItemReader<RawNamedPreset> itemReader = fileReader(rawPresetFieldSetMapper());
        itemReader.setLinesToSkip(headerLines);

        return stepBuilderFactory.get(WITH_FROM_DB_LOADING_STEP_NAME)
                .<RawNamedPreset, RawNamedPreset>chunk(chunkSize)
                .faultTolerant()
                .skipLimit(SKIP_LIMIT)
                .<RawNamedPreset>reader(rawPresetMultiFileReader(resources, itemReader))
                .processor(compositeItemProcessor(
                        rawPresetValidator(),
                        setPresetRelevancy(defaults)))
                .writer(rawItemList -> rawItemList.forEach(rawItem -> {
                    presets.withFrom.addPreset(
                            PresetItemBuilder.createWithName(rawItem.name)
                                    .withDescription(rawItem.description)
                                    .withRelevancy(rawItem.relevancy)
                                    .build());
                }))
                .listener(new LogStepListener())
                .build();
    }

    private ItemProcessor<RawNamedPreset, RawNamedPreset> setPresetRelevancy(List<String> validPresetNames) {
        return rawNamedPreset -> {
            int relevancy = validPresetNames.indexOf(rawNamedPreset.name);
            if (relevancy >= 0) {
                rawNamedPreset.relevancy = relevancy;
                return rawNamedPreset;
            } else {
                return INVALID_PRESET;
            }
        };
    }

    private FieldSetMapper<RawNamedPreset> rawPresetFieldSetMapper() {
        return new StringToRawNamedPresetMapper(SourceColumnsFactory.createFor(DB_COLUMNS));
    }

    private ItemProcessor<RawNamedPreset, RawNamedPreset> rawPresetValidator() {
        return new RawNamedPresetValidator();
    }
}
